package com.github.vincemann.springrapid.auth;

import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.RequestMediumChangeDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import javax.validation.constraints.NotBlank;

public interface MessageSender {

    public void resendVerificationMessage(U user) throws EntityNotFoundException, BadEntityException;

    public void sendForgotPasswordMessage( @NotBlank String email) throws EntityNotFoundException;

    // use dto here so https encrypts new password which is not possible via url param
    // target email gets extracted from code
    public U sendResetPasswordMessage(ResetPasswordDto dto, String code) throws EntityNotFoundException,  BadEntityException;
    public void sendChangePasswordMessage(U user, ChangePasswordDto changePasswordForm) throws EntityNotFoundException, BadEntityException;

    //    @Validated(UserVerifyUtils.ChangeEmailValidation.class)
    public void requestMediumChange(U user, RequestMediumChangeDto emailChangeForm) throws EntityNotFoundException, AlreadyRegisteredException;

    // get user from email from code
    public U changeMedium(/*U user,*/  @NotBlank String changeMediumCode) throws EntityNotFoundException, BadEntityException;

}
