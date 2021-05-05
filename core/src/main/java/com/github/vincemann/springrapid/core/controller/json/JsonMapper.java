package com.github.vincemann.springrapid.core.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

import java.nio.file.AccessDeniedException;

public interface JsonMapper {


    <T> T readDto(String json, Class<T> dtoClass) throws  JsonProcessingException;
    <T> T readDto(String json, JavaType dtoClass) throws JsonProcessingException;
    <T> T readDto(String json, TypeReference<?> dtoClass) throws JsonProcessingException;
    String writeDto(Object dto) throws JsonProcessingException;
}
