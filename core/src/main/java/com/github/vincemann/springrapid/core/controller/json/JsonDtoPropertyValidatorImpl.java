package com.github.vincemann.springrapid.core.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



public class JsonDtoPropertyValidatorImpl implements JsonDtoPropertyValidator {

    private ObjectMapper objectMapper;


    @Override
    public void validateDto(String jsonDto, Class dtoClass/*, Class entityClass*/) throws AccessDeniedException, JsonProcessingException {
        Iterator<String> propertyNameIterator = objectMapper.readTree(jsonDto).fieldNames();
        checkPropertyNames(propertyNameIterator, dtoClass/*,entityClass*/);
    }

    @Override
    public void validatePatch(String patch, Class dtoClass/*, Class entityClass*/) throws AccessDeniedException, JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(patch);
        List<JsonNode> pathNodes = rootNode.findValues("path");
        Set<String> propertyNames = pathNodes.stream()
                .map(jsonNode -> sanitizePatchStringPathProperty(jsonNode.asText()))
                .collect(Collectors.toSet());
        Iterator<String> propertyNameIterator = propertyNames.iterator();
        checkPropertyNames(propertyNameIterator, dtoClass/*,entityClass*/);
    }



    protected String sanitizePatchStringPathProperty(String path) {
        path = path.replace("/", "");
        path = path.replace("-", "");
        return path;
    }

    protected void checkPropertyNames(Iterator<String> propertyNameIterator, Class dtoClass/*, Class entityClass*/) {
        Set<String> dtoClassFieldNames = new HashSet<>();
        ReflectionUtils.doWithFields(dtoClass, field -> dtoClassFieldNames.add(field.getName()), ReflectionUtils.COPYABLE_FIELDS);
        while (propertyNameIterator.hasNext()) {
            String dtoProperty = propertyNameIterator.next();
            if (!dtoClassFieldNames.contains(dtoProperty)) {
                // property not allowed
                throw new org.springframework.security.access.AccessDeniedException("Dto Property: " + dtoProperty + " is not allowed for this operation. Dto class: " + dtoClass.getSimpleName());
            }
        }
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
