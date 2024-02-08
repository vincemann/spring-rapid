package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.dto.SignupAdminDto;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface SignupService {

    public AbstractUser signup(@Valid SignupDto signupDto) throws BadEntityException, AlreadyRegisteredException;
    public AbstractUser signupAdmin(@Valid SignupAdminDto signupDto) throws BadEntityException;
}
