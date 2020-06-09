package com.github.vincemann.springrapid.core.service.exception;

/**
 * Indicates that the client sent an Entity that was malformed.
 */
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
