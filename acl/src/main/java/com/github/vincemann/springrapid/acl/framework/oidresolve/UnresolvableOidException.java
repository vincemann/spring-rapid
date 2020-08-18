package com.github.vincemann.springrapid.acl.framework.oidresolve;

public class UnresolvableOidException extends Exception{
    public UnresolvableOidException() {
    }

    public UnresolvableOidException(String message) {
        super(message);
    }

    public UnresolvableOidException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnresolvableOidException(Throwable cause) {
        super(cause);
    }

    public UnresolvableOidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
