package io.github.vincemann.generic.crud.lib.controller.exception;

/**
 * Indicates, that a mapping from a DTO Entity to ServiceEntity with {@link io.github.vincemann.generic.crud.lib.dtoMapper.DTOMapper} or vice versa failed.
 */
public class EntityMappingException extends Exception {


    public EntityMappingException() {
    }

    public EntityMappingException(String message) {
        super(message);
    }

    public EntityMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityMappingException(Throwable cause) {
        super(cause);
    }

    public EntityMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
