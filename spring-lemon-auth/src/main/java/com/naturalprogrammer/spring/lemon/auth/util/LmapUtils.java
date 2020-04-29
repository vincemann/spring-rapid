package com.naturalprogrammer.spring.lemon.auth.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonUserDto;
import com.naturalprogrammer.spring.lemon.auth.security.service.BlueTokenService;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;

public class LmapUtils {


    public static ObjectMapper objectMapper;

    public LmapUtils(ObjectMapper objectMapper) {
        LmapUtils.objectMapper = objectMapper;
    }

    public static ObjectMapper mapper() {

        return objectMapper;
    }

    /**
     * Applies a JsonPatch to an object
     */
    @SuppressWarnings("unchecked")
    public static <T> T applyPatch(T originalObj, String patchString)
            throws JsonProcessingException, IOException, JsonPatchException {

        // Parse the patch to JsonNode
        JsonNode patchNode = objectMapper.readTree(patchString);

        // Create the patch
        JsonPatch patch = JsonPatch.fromJson(patchNode);

        // Convert the original object to JsonNode
        JsonNode originalObjNode = objectMapper.valueToTree(originalObj);

        // Apply the patch
        TreeNode patchedObjNode = patch.apply(originalObjNode);

        // Convert the patched node to an updated obj
        return objectMapper.treeToValue(patchedObjNode, (Class<T>) originalObj.getClass());
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

    /**
     * Serializes an object
     */
    public static String serialize(Serializable obj) {

        return Base64.getUrlEncoder().encodeToString(
                SerializationUtils.serialize(obj));
    }

    /**
     * Deserializes an object
     */
    public static <T> T deserialize(String serializedObj) {

        return SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(serializedObj));
    }

    public static LemonUserDto getUserDto(JWTClaimsSet claims) {

        Object userClaim = claims.getClaim(BlueTokenService.USER_CLAIM);

        if (userClaim == null)
            return null;

        return LmapUtils.deserialize((String) userClaim);
    }
}
