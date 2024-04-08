package com.github.vincemann.springrapid.auth.service.val;

import com.github.vincemann.springrapid.auth.ex.BadEntityException;

public interface ContactInformationValidator {
    public void validate(String contactInformation) throws BadEntityException;
}
