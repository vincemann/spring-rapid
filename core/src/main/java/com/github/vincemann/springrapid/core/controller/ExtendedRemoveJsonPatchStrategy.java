package com.github.vincemann.springrapid.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.IdPropertyNameUtils;

import com.github.vincemann.springrapid.core.util.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

import static com.github.vincemann.springrapid.core.util.ReflectionUtils.setFinal;

/**
 * Allows removing by value for string convertable type Collections and {@link IdentifiableEntity} Collections.
 * Translates value to index.
 *
 * i.E:
 * petIds= [4,10,2,5,7]
 *
 * op 'remove', path '/petIds', value '5'
 * ->
 * op 'remove', path '/petIds/3'
 *
 *
 * NOTE::
 * Don't use remove and add in one request, if you remove whole collection and not elements by value/index.
 * This is a limitation by JsonPatch itself, not by this extension.
 */
@Slf4j
public class ExtendedRemoveJsonPatchStrategy implements JsonPatchStrategy {
    
    private ObjectMapper objectMapper;

    @Override
    public PatchInfo findPatchInfo(String patchString) throws BadEntityException {
        try {
            PatchInfo patchInfo = new PatchInfo();
            JsonNode patchNode = objectMapper.readTree(patchString);
            Iterator<JsonNode> iterator = patchNode.elements();
            while (iterator.hasNext()){
                JsonNode instructionNode = iterator.next();
                JsonNode operationNode = instructionNode.findValue("op");
                JsonNode pathNode = instructionNode.findValue("path");
                String path = pathNode.asText();
                if (operationNode!=null){
                    String operation = operationNode.asText();
                    if (operation.equals("remove")) {
                        JsonNode valueNode = instructionNode.findValue("value");
                        if (valueNode == null) {
                            // remove single member, not collection
                            if (path == null) {
                                throw new BadEntityException("No Path specified to remove operation");
                            }
                            patchInfo.getRemoveSingleMembersFields().add(sanitizePath(path));
                        }
                    }
                }

                if (path == null) {
                    continue;
                }
                patchInfo.getUpdatedFields().add(sanitizePath(path));
            }
            return patchInfo;
        } catch (JsonProcessingException e) {
            throw new BadEntityException(e);
        }
    }

    private static String sanitizePath(String path){
        return path.replace("-","").replace("/","");
    }

    @Override
    public <T> T applyPatch(T targetDto, String patchString) throws BadEntityException {
        try {

            // Parse the patch to JsonNode
            JsonNode patchNode = objectMapper.readTree(patchString);

            // Create the patch
            JsonPatch patch;
            try {
                patch = createPatch(targetDto, patchNode);
            } catch (Exception e) {
                throw new BadEntityException(e);
            }

            // Convert the original object to JsonNode
            JsonNode originalObjNode = objectMapper.valueToTree(targetDto);

            // Apply the patch
            TreeNode patchedObjNode = patch.apply(originalObjNode);

            // Convert the patched node to an updated obj
            return objectMapper.treeToValue(patchedObjNode, (Class<T>) targetDto.getClass());
        } catch (JsonProcessingException | JsonPatchException e) {
            throw new BadEntityException(e);
        }
    }

    private JsonPatch createPatch(Object dto, JsonNode patchNode) throws Exception {
        Map<Collection, List<Integer>> removedIndicesMap = new HashMap<>();

        Iterator<JsonNode> iterator = patchNode.elements();
        while (iterator.hasNext()){
            JsonNode instructionNode = iterator.next();
            JsonNode operationNode = instructionNode.findValue("op");
            if (operationNode==null){
                continue;
            }
            String operation = operationNode.asText();
            if (operation.equals("remove")) {
                JsonNode valueNode = instructionNode.findValue("value");
                if (valueNode != null) {
                    patchRemoveInstruction(instructionNode,valueNode,dto,removedIndicesMap);
                }
            }
        }
        return JsonPatch.fromJson(patchNode);
    }

    private void patchRemoveInstruction(JsonNode instructionNode, JsonNode valueNode,Object dto, Map<Collection, List<Integer>> removedIndicesMap) throws Exception {
        log.debug("found update-remove operation with value set");
        String value = valueNode.asText();
        String path = instructionNode.findValue("path").asText();
        Field collectionField = ReflectionUtils.findField(dto.getClass(),
                // Utils wont transform if not "...Ids" fieldname
                path.replace("/", ""));
        if (collectionField==null){
            log.warn("Collection field for remove by value not found: "+ path);
            return;
        }
        collectionField.setAccessible(true);

        int[] position = {-1};
        Optional elementToDelete = Optional.empty();
        Collection collection;
//        if (IdPropertyNameUtils.isCollectionIdField(path.replace("/", ""))) {
        log.debug("removing from entity collection, value will be interpreted as id");
        collection = (Collection) collectionField.get(dto);
        elementToDelete = ((Collection<?>)collection).stream()
                .peek(x -> position[0]++)  // increment every element encounter
                .filter(o -> o.toString().equals(value))
                .findFirst();
//        }
//        else {
//            log.debug("removing from normal collection (assuming comparable by String Type)");
//            collection = (Collection) collectionField.get(savedEntity);
//            elementToDelete = collection.stream()
//                    .peek(x -> position[0]++)  // increment every element encounter
//                    .filter(o -> o.toString().equals(value))
//                    .findFirst();
//        }

        if (elementToDelete.isEmpty()) {
            throw new IllegalArgumentException("Element to delete: "+value+" not found");
        }
        int index = position[0];
        List<Integer> removedIndices = getRemovedIndices(removedIndicesMap, collection);
        int adjustedIndex = adjustIndex(index,removedIndices);
        removedIndices.add(index);

        log.debug("found index: " + index);
        log.debug("adjusted index: " + adjustedIndex);
        // modify JsonNode
        TextNode pathNode = (TextNode) instructionNode.findValue("path");
        Field pathValueField = ReflectionUtils.findField(TextNode.class, "_value");
        setFinal(pathValueField, pathNode, path + "/" + Long.toString(adjustedIndex));
    }

    private List<Integer> getRemovedIndices(Map<Collection, List<Integer>> removedIndicesMap, Collection collection){
        List<Integer> removedIndices = removedIndicesMap.get(collection);
        if (removedIndices==null){
            removedIndices = Lists.newArrayList();
            removedIndicesMap.put(collection,removedIndices);
        }
        return removedIndices;
    }

    /**
     *  When Json Patch performs multiple remove operations on one collection,
     *  it performs the first, then the second on the already updated collection, which has lower size now.
     *  But it also removes via index, so indices might change.
     *  This function adjusts the index
     */
    private int adjustIndex(int index, List<Integer> removedIndices){
        // find amount deleted elements before my index
        int removedBeforeMe = 0;
        for (Integer removedIndex : removedIndices) {
            if (removedIndex<index){
                removedBeforeMe++;
            }
        }
        return index-removedBeforeMe;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
