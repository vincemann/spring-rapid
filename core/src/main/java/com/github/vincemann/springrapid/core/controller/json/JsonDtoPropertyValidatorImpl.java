package com.github.vincemann.springrapid.core.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.util.ReflectionUtils;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// todo maybe create similar solution with mask of allowed properties for user on service level
// acl could not be entity wide but entity.property wide
// otherwise security business logic will be partly in controller layer
@Setter
public class JsonDtoPropertyValidatorImpl implements JsonDtoPropertyValidator {

    private ObjectMapper objectMapper;

    public JsonDtoPropertyValidatorImpl() {
        this.objectMapper = new ObjectMapper();
    }

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
//        Set<String> entityClassFieldNames = new HashSet<>();
        Set<String> dtoClassFieldNames = new HashSet<>();
        // do like this bc spring chaches
//        ReflectionUtils.doWithFields(entityClass, field -> {
//            entityClassFieldNames.add(field.getName());
//        });
        ReflectionUtils.doWithFields(dtoClass, field -> {
            dtoClassFieldNames.add(field.getName());
        });
//         = Arrays.stream(entityClass.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
//        = Arrays.stream(dtoClass.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());
        while (propertyNameIterator.hasNext()) {
            String dtoProperty = propertyNameIterator.next();

            // rather ignore, bc mappings are not always name -> name or userId -> user
//            if (!entityClassFieldNames.contains(EntityCollectionNameUtils.transformDtoEntityIdFieldName(dtoProperty)) &&
//                    !entityClassFieldNames.contains(EntityCollectionNameUtils.transformDtoEntityIdCollectionFieldName(dtoProperty))){
//                // unknown property
//                throw new BadEntityException("Dto Property: " + dtoProperty + " is unknown");
//            }
            if (!dtoClassFieldNames.contains(dtoProperty)) {
                // property not allowed
                throw new org.springframework.security.access.AccessDeniedException("Dto Property: " + dtoProperty + " is not allowed for this operation");
            }
        }
    }
}
