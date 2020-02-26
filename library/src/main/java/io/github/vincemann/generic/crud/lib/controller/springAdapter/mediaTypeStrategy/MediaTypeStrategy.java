package io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;
import java.util.Collection;

/**
 * Interface for 'how to convert the stringBody of an HttpRequest to a Dto Entity
 */
public interface MediaTypeStrategy {
    /**
     *
     * @param body      body of HttpRequest in String form
     * @param dtoClass  class of the Dto Entity
     * @param <Dto>     Type of Dto Entity
     * @return          Dto entity
     * @throws ProcessDtoException      occurs, when Dto could not be fetched from StringBody
     */
    <Dto extends IdentifiableEntity> Dto readDto(String body, Class<Dto> dtoClass) throws ProcessDtoException;

    <Dto extends IdentifiableEntity, C extends Collection<Dto>> C readDtos(String body, Class<Dto> dtoClass, Class<C> collectionType) throws ProcessDtoException;

    <Dto extends IdentifiableEntity> String writeDto(Dto entity) throws ProcessDtoException;
    /**
     * Use {@link org.springframework.http.MediaType}
     * @return     MediaType used to represent Dto in the body (mostly JSON, or XML)
     */
    String getMediaType();


}
