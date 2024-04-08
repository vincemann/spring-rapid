package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.jwt.BadTokenException;
import com.github.vincemann.springrapid.auth.val.InsufficientPasswordStrengthException;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;

public interface PasswordService {

    public AbstractUser forgotPassword(String contactInformation) throws EntityNotFoundException, BadEntityException;

    public AbstractUser resetPassword(ResetPasswordDto resetPasswordDto) throws EntityNotFoundException, BadEntityException, BadTokenException, InsufficientPasswordStrengthException;
    public AbstractUser changePassword(ChangePasswordDto changePasswordDto) throws EntityNotFoundException, BadEntityException, InsufficientPasswordStrengthException;

}
