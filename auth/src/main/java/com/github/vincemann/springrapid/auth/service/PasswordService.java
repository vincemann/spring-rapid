package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Validated
public interface PasswordService {

    public AbstractUser forgotPassword(@NotBlank String contactInformation) throws EntityNotFoundException;

    public AbstractUser resetPassword(@Valid ResetPasswordDto resetPasswordDto) throws EntityNotFoundException, BadEntityException, BadTokenException;
    public AbstractUser changePassword(@Valid ChangePasswordDto changePasswordDto) throws EntityNotFoundException, BadEntityException;

}
