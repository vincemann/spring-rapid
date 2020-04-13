package io.github.vincemann.springrapid.core.controller.rapid;

/**
 * Indicates that a Dto could not be read (de-serialized) from the raw JSON String.
 */
public class DtoSerializingException extends Exception{

    public DtoSerializingException() {
    }

    public DtoSerializingException(String message) {
        super(message);
    }

    public DtoSerializingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DtoSerializingException(Throwable cause) {
        super(cause);
    }

    public DtoSerializingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
