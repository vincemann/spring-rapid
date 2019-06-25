package io.github.vincemann.generic.crud.lib.controller.exception;

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
