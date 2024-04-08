package com.github.vincemann.springrapid.auth.val;

public interface PasswordValidator {
    public void validate(String password) throws InsufficientPasswordStrengthException;
}
