package com.github.vincemann.springrapid.auth.service.val;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

public interface PasswordValidator {
    public void validate(String password) throws BadEntityException;
}
