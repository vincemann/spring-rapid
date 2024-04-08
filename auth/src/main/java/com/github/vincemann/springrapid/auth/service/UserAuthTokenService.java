package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;

public interface UserAuthTokenService {
    public String createNewAuthToken(String contactInformation) throws EntityNotFoundException, BadEntityException;
    public String createNewAuthToken() throws EntityNotFoundException;
}
