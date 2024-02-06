package com.github.vincemann.springrapid.acl.service;

public class AceNotFoundException extends RuntimeException{
    public AceNotFoundException() {
    }

    public AceNotFoundException(String message) {
        super(message);
    }

    public AceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AceNotFoundException(Throwable cause) {
        super(cause);
    }

    public AceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
