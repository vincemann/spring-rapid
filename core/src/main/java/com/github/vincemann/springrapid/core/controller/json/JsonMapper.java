package com.github.vincemann.springrapid.core.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

import java.nio.file.AccessDeniedException;

public interface JsonMapper {

    Object readDto(String json, Class dtoClass, Class entityClass) throws BadEntityException, AccessDeniedException, JsonProcessingException;
    String writeDto(Object dto) throws JsonProcessingException;
}
