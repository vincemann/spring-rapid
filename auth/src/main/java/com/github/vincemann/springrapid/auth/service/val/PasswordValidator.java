package com.github.vincemann.springrapid.auth.service.val;

import com.github.vincemann.springrapid.auth.ex.InsufficientPasswordStrengthException;

public interface PasswordValidator {
    public void validate(String password) throws InsufficientPasswordStrengthException;
}
