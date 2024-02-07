package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.controller.dto.SignupDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

public interface SignupService<U extends AbstractUser, Dto extends SignupDto> {

    public U signup( Dto signupDto) throws BadEntityException, AlreadyRegisteredException;
    public U signupAdmin( Dto signupDto);
}
