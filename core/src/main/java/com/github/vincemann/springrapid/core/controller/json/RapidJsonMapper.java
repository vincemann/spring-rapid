package com.github.vincemann.springrapid.core.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.AccessDeniedException;

public class RapidJsonMapper implements JsonMapper {

    private ObjectMapper objectMapper;
    private JsonDtoPropertyValidator jsonDtoPropertyValidator;

    @Override
    public Object readDto(String json, Class dtoClass, Class entityClass) throws JsonProcessingException, BadEntityException, AccessDeniedException {
        jsonDtoPropertyValidator.validateDto(json,dtoClass,entityClass);
        return objectMapper.readValue(json,dtoClass);
    }

    @Override
    public String writeDto(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }



    @Autowired
    public void setJsonDtoValidator(JsonDtoPropertyValidator jsonDtoPropertyValidator) {
        this.jsonDtoPropertyValidator = jsonDtoPropertyValidator;
    }

    @Autowired
    public void injectObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
