package com.github.vincemann.springrapid.limitsaves;

import org.springframework.security.access.AccessDeniedException;

public class TooManyRequestsException extends AccessDeniedException {
    public TooManyRequestsException(String msg) {
        super(msg);
    }

    public TooManyRequestsException(String msg, Throwable t) {
        super(msg, t);
    }
}
