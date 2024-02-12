package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

public interface UserAuthTokenService {
    public String createNewAuthToken(String contactInformation) throws EntityNotFoundException;
    public String createNewAuthToken() throws EntityNotFoundException;
}
