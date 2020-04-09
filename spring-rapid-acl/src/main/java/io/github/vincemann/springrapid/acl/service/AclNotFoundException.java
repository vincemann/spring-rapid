package io.github.vincemann.springrapid.acl.service;

public class AclNotFoundException extends Exception {

    public AclNotFoundException() {
    }

    public AclNotFoundException(String message) {
        super(message);
    }

    public AclNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AclNotFoundException(Throwable cause) {
        super(cause);
    }

    public AclNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
