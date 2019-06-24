package vincemann.github.generic.crud.lib.controller.springAdapter.mediaTypeStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
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
            mapper.convertValue(body, type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public String getMediaType() {
        return MediaType.APPLICATION_JSON_VALUE;
    }
}
