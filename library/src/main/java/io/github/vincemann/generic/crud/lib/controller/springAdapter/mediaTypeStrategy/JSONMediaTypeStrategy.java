package io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Collection;

public class JSONMediaTypeStrategy implements MediaTypeStrategy{
    private final ObjectMapper mapper;

    public JSONMediaTypeStrategy() {
        this.mapper= new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public <Dto> Dto readDtoFromBody(String body, Class<Dto> dtoClass) throws DtoReadingException {
        try {
            return mapper.readValue(body, dtoClass);
        } catch (IOException e) {
            throw new DtoReadingException(e);
        }
    }

    @Override
    public <Dto, C extends Collection<Dto>> C readDtosFromBody(String body, Class<Dto> dtoClass, Class<C> collectionType) throws DtoReadingException {
        try {
            return mapper.readValue(body,mapper.getTypeFactory().constructCollectionType(collectionType,dtoClass));
        } catch (IOException e) {
            throw new DtoReadingException(e);
        }
    }

    @Override
    public boolean isBodyOfGivenType(String body, Class type) {
        try {
            JSONObject jObj = new JSONObject(body);
            Object aObj = jObj.get(type.getSimpleName());
            return aObj.getClass().equals(type);
        } catch (IllegalArgumentException|JSONException e) {
            return false;
        }
    }

    @Override
    public String getMediaType() {
        return MediaType.APPLICATION_JSON_VALUE;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }
}
