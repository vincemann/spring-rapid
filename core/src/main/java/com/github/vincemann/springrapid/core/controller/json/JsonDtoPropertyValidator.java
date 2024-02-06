package com.github.vincemann.springrapid.core.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.nio.file.AccessDeniedException;
import java.util.Set;

/**
 * Checks if user has dto property, that is not in target dto class.
 * If so, throws {@link AccessDeniedException}.
 */
public interface JsonDtoPropertyValidator {

    void validateDto(String json, Class dtoClass/*, Class entityClass*/) throws AccessDeniedException, JsonProcessingException;
    void validatePatch(String json, Class dtoClass/*, Class entityClass*/) throws AccessDeniedException, JsonProcessingException;

    void validateDtoPartly(Object patchDto, Set<String> updatedFields);
}
