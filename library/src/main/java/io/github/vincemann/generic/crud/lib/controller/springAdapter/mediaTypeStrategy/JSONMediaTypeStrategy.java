package io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import java.io.IOException;

public class JSONMediaTypeStrategy implements MediaTypeStrategy{
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public <DTO> DTO readDTOFromBody(String body,Class<DTO> dtoClass) throws DTOReadingException {
        try {
            return mapper.readValue(body, dtoClass);
        } catch (IOException e) {
            throw new DTOReadingException(e);
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
}
