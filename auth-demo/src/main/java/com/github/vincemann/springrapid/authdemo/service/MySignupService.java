package com.github.vincemann.springrapid.authdemo.service;

import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.authdemo.dto.SignupDto;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface MySignupService {
    User signup(@Valid SignupDto signupDto) throws BadEntityException, AlreadyRegisteredException;
}
