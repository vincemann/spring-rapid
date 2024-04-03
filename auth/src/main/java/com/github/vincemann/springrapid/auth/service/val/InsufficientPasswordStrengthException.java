package com.github.vincemann.springrapid.auth.service.val;

public class InsufficientPasswordStrengthException extends RuntimeException {
    public InsufficientPasswordStrengthException(String message) {
        super(message);
    }
}
