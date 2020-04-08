package io.github.vincemann.springrapid.core.controller.rapid.idFetchingStrategy.exception;

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
