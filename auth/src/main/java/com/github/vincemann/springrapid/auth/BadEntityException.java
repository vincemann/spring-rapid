package com.github.vincemann.springrapid.auth;

/**
 * Indicates bad request.
 */
public class BadEntityException extends RuntimeException {

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
