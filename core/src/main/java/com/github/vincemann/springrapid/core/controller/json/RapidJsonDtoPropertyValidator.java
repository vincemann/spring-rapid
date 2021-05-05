package com.github.vincemann.springrapid.core.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.EntityCollectionNameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RapidJsonDtoPropertyValidator implements JsonDtoPropertyValidator {

    private ObjectMapper objectMapper;

    @Override
    public void validateDto(String jsonDto, Class dtoClass, Class entityClass) throws BadEntityException, AccessDeniedException, JsonProcessingException {
        Iterator<String> propertyNameIterator = objectMapper.readTree(jsonDto).fieldNames();
        checkPropertyNames(propertyNameIterator,dtoClass,entityClass);
    }

    @Override
    public void validatePatch(String patch, Class dtoClass, Class entityClass) throws BadEntityException, AccessDeniedException, JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(patch);
        List<JsonNode> pathNodes = rootNode.findValues("path");
        Set<String> propertyNames = pathNodes.stream().map(JsonNode::asText).collect(Collectors.toSet());
        Iterator<String> propertyNameIterator = propertyNames.iterator();
        checkPropertyNames(propertyNameIterator,dtoClass,entityClass);
    }

    protected void checkPropertyNames(Iterator<String> propertyNameIterator, Class dtoClass, Class entityClass) throws BadEntityException {
        Set<String> entityClassFieldNames = new HashSet<>();
        Set<String> dtoClassFieldNames = new HashSet<>();
        // do like this bc spring chaches
        ReflectionUtils.doWithFields(entityClass, field -> {
            entityClassFieldNames.add(field.getName());
        });
        ReflectionUtils.doWithFields(dtoClass,field -> {
            dtoClassFieldNames.add(field.getName());
        });
//         = Arrays.stream(entityClass.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
//        = Arrays.stream(dtoClass.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
        while (propertyNameIterator.hasNext()){
            String dtoProperty = propertyNameIterator.next();
            if (!entityClassFieldNames.contains(EntityCollectionNameUtils.transformDtoEntityIdFieldName(dtoProperty)) &&
                    !entityClassFieldNames.contains(EntityCollectionNameUtils.transformDtoEntityIdCollectionFieldName(dtoProperty))){
                // unknown property
                throw new BadEntityException("Dto Property: " + dtoProperty + " is unknown");
            }
            if (!dtoClassFieldNames.contains(dtoProperty)){
                // property not allowed
                throw new org.springframework.security.access.AccessDeniedException("Dto Property: " + dtoProperty + " is not allowed for this operation");
            }
        }
    }

    @Autowired
    public void injectObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
