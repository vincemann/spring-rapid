package io.github.vincemann.springrapid.core.service.exception.entityRelationHandling;

public abstract class EntityRelationHandlingException extends RuntimeException{
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
