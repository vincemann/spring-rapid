package com.github.vincemann.springrapid.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.EntityCollectionUtils;
import com.github.vincemann.springrapid.core.util.JsonUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

import static com.github.vincemann.springrapid.core.util.ReflectionUtils.setFinal;

public class ExtendedRemoveJsonPatchStrategy implements JsonPatchStrategy {
    @Override
    public <T> T applyPatch(IdentifiableEntity savedEntity, T targetDto, String patchString) throws BadEntityException {
        try {

            // Parse the patch to JsonNode
            JsonNode patchNode = JsonUtils.mapper().readTree(patchString);

            // Create the patch
            JsonPatch patch = null;
            try {
                patch = createPatch(savedEntity, patchNode);
            } catch (Exception e) {
                throw new BadEntityException(e);
            }

            // Convert the original object to JsonNode
            JsonNode originalObjNode = JsonUtils.mapper().valueToTree(targetDto);

            // Apply the patch
            TreeNode patchedObjNode = patch.apply(originalObjNode);

            // Convert the patched node to an updated obj
            return JsonUtils.mapper().treeToValue(patchedObjNode, (Class<T>) targetDto.getClass());
        }catch (JsonProcessingException| JsonPatchException e) {
            throw new BadEntityException(e);
        }
    }


    private static JsonPatch createPatch(IdentifiableEntity savedEntity,JsonNode patchNode) throws Exception {
        // if patch node has remove, list, value set, then intervene
        // value will be interpreted as id
        // find index of targetObj.list[id]
        // return jsonPatch: remove path/foundIndex instead
        String operation = patchNode.findValue("op").asText();
        if (operation.equals("remove")){
            JsonNode valueNode = patchNode.findValue("value");
            if (valueNode!=null){
                String id = valueNode.asText();
                String path = patchNode.findValue("path").asText();
                Field collectionField = ReflectionUtils.findField(savedEntity.getClass(), EntityCollectionUtils.transformDtoCollectionFieldName(path.replace("/", "")));
                collectionField.setAccessible(true);
                Collection<? extends IdentifiableEntity> collection = (Collection) collectionField.get(savedEntity);
                int[] position = {-1};

                Optional<? extends IdentifiableEntity> elementToDelte = collection.stream()
                        .peek(x -> position[0]++)  // increment every element encounter
                        .filter(o -> o.getId().toString().equals(id))
                        .findFirst();
                if (elementToDelte.isEmpty()){
                    throw new IllegalArgumentException("Element to delete not found");
                }
                int index = position[0];
                // modify JsonNode
                TextNode pathNode = (TextNode)patchNode.findValue("path");
                Field pathValueField = ReflectionUtils.findField(TextNode.class, "_value");
                setFinal(pathValueField,pathNode,path+"/"+Long.toString(index));
                return JsonPatch.fromJson(patchNode);
            }
        }
        return JsonPatch.fromJson(patchNode);
    }

}
