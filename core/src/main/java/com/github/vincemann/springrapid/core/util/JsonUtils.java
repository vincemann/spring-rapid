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
