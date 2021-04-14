package com.github.vincemann.springrapid.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.RemoveOperation;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class JsonUtils {


    public static ObjectMapper objectMapper;

    public JsonUtils(ObjectMapper objectMapper) {
        JsonUtils.objectMapper = objectMapper;
    }

    public static ObjectMapper mapper() {

        return objectMapper;
    }

    /**
     * Applies a JsonPatch to an object
     */
    @SuppressWarnings("unchecked")
    public static <T> T applyPatch(IdentifiableEntity savedEntity, T originalObj, String patchString)
            throws JsonProcessingException, IOException, JsonPatchException {

        // Parse the patch to JsonNode
        JsonNode patchNode = objectMapper.readTree(patchString);

        // Create the patch
        JsonPatch patch = null;
        try {
            patch = createPatch(savedEntity,patchNode);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JsonPatchException(e.getMessage());
        }

        // Convert the original object to JsonNode
        JsonNode originalObjNode = objectMapper.valueToTree(originalObj);

        // Apply the patch
        TreeNode patchedObjNode = patch.apply(originalObjNode);

        // Convert the patched node to an updated obj
        return objectMapper.treeToValue(patchedObjNode, (Class<T>) originalObj.getClass());
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
                Long id = valueNode.asLong();
                String path = patchNode.findValue("path").asText();
                Field collectionField = ReflectionUtils.findField(savedEntity.getClass(), EntityCollectionUtils.transformDtoCollectionFieldName(path.replace("/", "")));
                collectionField.setAccessible(true);
                Collection<? extends IdentifiableEntity> collection = (Collection) collectionField.get(savedEntity);
                int[] position = {-1};

                Optional<? extends IdentifiableEntity> elementToDelte = collection.stream()
                        .peek(x -> position[0]++)  // increment every element encounter
                        .filter(o -> o.getId().equals(id))
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

    static void setFinal(Field field,Object target, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(target, newValue);
    }


    /**
     * Serializes an object to JSON string
     */
    public static <T> String toJson(T obj) {

        try {

            return objectMapper.writeValueAsString(obj);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(e);
        }
    }


    /**
     * Deserializes a JSON String
     */
    public static <T> T fromJson(String json, Class<T> clazz) {

        try {

            return objectMapper.readValue(json, clazz);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

//    /**
//     * Serializes an object
//     */
//    public static String serialize(Serializable obj) {
//
//        return Base64.getUrlEncoder().encodeToString(
//                SerializationUtils.serialize(obj));
//    }
//
//    /**
//     * Deserializes an object
//     */
//    public static <T> T deserialize(String serializedObj) {
//
//        return SerializationUtils.deserialize(
//                Base64.getUrlDecoder().decode(serializedObj));
//    }

}
