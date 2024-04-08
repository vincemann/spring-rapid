package com.github.vincemann.springrapid.auth.val;

public class InsufficientPasswordStrengthException extends RuntimeException {
    public InsufficientPasswordStrengthException(String message) {
        super(message);
    }
}
