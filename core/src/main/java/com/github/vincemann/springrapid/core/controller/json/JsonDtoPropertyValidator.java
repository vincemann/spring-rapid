package com.github.vincemann.springrapid.core.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

import java.nio.file.AccessDeniedException;

public interface JsonDtoPropertyValidator {

    void validate(String json, Class dtoClass, Class entityClass) throws BadEntityException, AccessDeniedException, JsonProcessingException;
}
