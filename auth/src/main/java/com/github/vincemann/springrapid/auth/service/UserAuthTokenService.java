package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

public interface UserAuthTokenService {
    public String createNewAuthToken(String contactInformation) throws EntityNotFoundException, BadEntityException;
    public String createNewAuthToken() throws EntityNotFoundException, BadEntityException;
}
