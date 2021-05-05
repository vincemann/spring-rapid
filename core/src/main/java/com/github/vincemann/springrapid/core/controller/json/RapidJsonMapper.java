package com.github.vincemann.springrapid.core.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.AccessDeniedException;

public class RapidJsonMapper implements JsonMapper {

    private ObjectMapper objectMapper;

    @Override
    public <T> T readDto(String json, Class<T> dtoClass) throws JsonProcessingException {
        return objectMapper.readValue(json,dtoClass);
    }

    @Override
    public <T> T readDto(String json, JavaType dtoClass) throws JsonProcessingException {
        return objectMapper.readValue(json,dtoClass);

    }

    @Override
    public <T> T readDto(String json, TypeReference<?> dtoClass) throws JsonProcessingException {
        return (T) objectMapper.readValue(json,dtoClass);
    }


    @Override
    public String writeDto(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }


    @Autowired
    public void injectObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
