package com.github.vincemann.springrapid.autobidir;

/**
 * Indicates that the automatic relationship handling of BiDir- or UniDir- Entities failed.
 */
public class AutoHandleEntityRelationShipException extends RuntimeException{
    public AutoHandleEntityRelationShipException(String message) {
        super(message);
    }

    public AutoHandleEntityRelationShipException(String message, Throwable cause) {
        super(message, cause);
    }

    public AutoHandleEntityRelationShipException(Throwable cause) {
        super(cause);
    }
}
