package io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.exception;

/**
 * Indicates, that the fetched id could not be transformed into the desired Type.
 * @see io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.IdFetchingStrategy
 */
public class IdTransformingException extends Exception{

    public IdTransformingException() {
    }

    public IdTransformingException(String message) {
        super(message);
    }

    public IdTransformingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdTransformingException(Throwable cause) {
        super(cause);
    }

    public IdTransformingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
