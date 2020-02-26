package io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

public class JSONMediaTypeStrategy implements MediaTypeStrategy{
    private final ObjectMapper mapper;

    public JSONMediaTypeStrategy() {
        this.mapper= new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.getDeserializationConfig().with(MapperFeature.USE_STATIC_TYPING);
    }

    @Override
    public <Dto extends IdentifiableEntity> Dto readDto(String body, Class<Dto> dtoClass) throws ProcessDtoException {
        try {
            return mapper.readValue(body, dtoClass);
        } catch (IOException e) {
            throw new ProcessDtoException(e);
        }
    }

    @Override
    public <Dto extends IdentifiableEntity> String writeDto(Dto entity) throws ProcessDtoException {
        try {
            return mapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new ProcessDtoException(e);
        }
    }

    @Override
    public <Dto extends IdentifiableEntity, C extends Collection<Dto>> C readDtos(String body, Class<Dto> dtoClass, Class<C> collectionType) throws ProcessDtoException {
        try {
            return mapper.readValue(body,mapper.getTypeFactory().constructCollectionType(collectionType,dtoClass));
        } catch (IOException e) {
            throw new ProcessDtoException(e);
        }
    }

//    @Override
//    public boolean isBodyOfGivenType(String body, Class type) {
//        try {
//            JSONObject jObj = new JSONObject(body);
//            Object aObj = jObj.get(type.getSimpleName());
//            return aObj.getClass().equals(type);
//        } catch (IllegalArgumentException|JSONException e) {
//            return false;
//        }
//    }

    @Override
    public String getMediaType() {
        return "application/json;charset=UTF-8";
    }

    public ObjectMapper getMapper() {
        return mapper;
    }
}
