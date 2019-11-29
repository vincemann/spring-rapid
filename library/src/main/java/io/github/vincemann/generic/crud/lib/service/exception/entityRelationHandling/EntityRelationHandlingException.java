package io.github.vincemann.generic.crud.lib.service.exception.entityRelationHandling;

public class EntityRelationHandlingException extends RuntimeException{
    public EntityRelationHandlingException(String message) {
        super(message);
    }

    public EntityRelationHandlingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityRelationHandlingException(Throwable cause) {
        super(cause);
    }
}
