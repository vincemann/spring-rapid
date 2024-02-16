package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
public interface UserAuthTokenService {
    public String createNewAuthToken(@NotBlank String contactInformation) throws EntityNotFoundException;
    public String createNewAuthToken() throws EntityNotFoundException;
}
