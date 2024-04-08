package com.github.vincemann.springrapid.auth.ex;

public class InsufficientPasswordStrengthException extends RuntimeException {
    public InsufficientPasswordStrengthException(String message) {
        super(message);
    }
}
