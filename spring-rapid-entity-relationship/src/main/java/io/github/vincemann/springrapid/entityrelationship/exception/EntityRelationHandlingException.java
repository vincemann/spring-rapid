package io.github.vincemann.springrapid.entityrelationship.exception;

/**
 * Indicates that the automatic relationship handling of BiDir- or UniDir- Entities failed.
 */
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
