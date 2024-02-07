package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.controller.dto.SignupDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface SignupService<U extends AbstractUser, Dto extends SignupDto> {

    public U signup(@Valid Dto signupDto) throws BadEntityException, AlreadyRegisteredException;
    public U signupAdmin(@Valid Dto signupDto);
}
