package com.github.vincemann.springrapid.auth.val;

import com.github.vincemann.springrapid.auth.BadEntityException;

public interface ContactInformationValidator {
    public void validate(String contactInformation) throws BadEntityException;
}
