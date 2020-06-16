package com.github.vincemann.springrapid.core.controller.dtoMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springrapid.core.advice.log.LogInteraction;

public class LoggingObjectMapper extends ObjectMapper {

    //@LogInteraction
    @Override
    public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException {
        return super.readValue(content, valueType);
    }

    //@LogInteraction
    @Override
    public <T> T readValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException, JsonMappingException {
        return super.readValue(content, valueTypeRef);
    }



    //@LogInteraction
    @Override
    public <T> T readValue(String content, JavaType valueType) throws JsonProcessingException {
        return super.readValue(content, valueType);
    }

    //@LogInteraction
    @Override
    public String writeValueAsString(Object value) throws JsonProcessingException {
        return super.writeValueAsString(value);
    }
}
