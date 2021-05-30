package com.github.vincemann.springrapid.auth.service.validation;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

// i like to keep it oldscool programmatically in a lib so impl can easily be switched out
public interface PasswordValidator {
    public void validate(String password) throws BadEntityException;
}
