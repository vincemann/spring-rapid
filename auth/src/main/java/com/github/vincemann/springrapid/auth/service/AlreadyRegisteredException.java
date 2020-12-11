package com.github.vincemann.springrapid.auth.service;

public class AlreadyRegisteredException extends Exception{
    public AlreadyRegisteredException() {
    }

    public AlreadyRegisteredException(String message) {
        super(message);
    }

    public AlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyRegisteredException(Throwable cause) {
        super(cause);
    }

    public AlreadyRegisteredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
