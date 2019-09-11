package io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy;

import java.util.Collection;

/**
 * Interface for 'how to convert the stringBody of an HttpRequest to a DTO Entity
 */
public interface MediaTypeStrategy{
    /**
     *
     * @param body      body of HttpRequest
     * @param dtoClass  class of the DTO Entity
     * @param <DTO>     Type of DTO Entity
     * @return          DTO entity
     * @throws DTOReadingException      occurs, when DTO could not be fetched from StringBody
     */
    <DTO> DTO readDTOFromBody(String body,Class<DTO> dtoClass) throws DTOReadingException;

    <DTO, C extends Collection<DTO>> C readDTOsFromBody(String body, Class<DTO> dtoClass, Class<C> collectionType) throws DTOReadingException;

    /**
     * Use {@link org.springframework.http.MediaType}
     * @return     MediaType used to represent DTO in the body (mostly JSON, or XML)
     */
    String getMediaType();

    /**
     *
     * @param body      body of HttpRequest
     * @param type      type to be checked
     * @return
     */
    boolean isBodyOfGivenType(String body, Class type);
}
