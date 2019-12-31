package io.github.vincemann.generic.crud.lib.service.exception;

public class BadEntityException extends Exception {

    public BadEntityException() {
    }

    public BadEntityException(String message) {
        super(message);
    }

    public BadEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadEntityException(Throwable cause) {
        super(cause);
    }

    public BadEntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
