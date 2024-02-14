package com.github.vincemann.springrapid.auth.service.ext.sec;

import com.github.vincemann.springrapid.acl.service.ext.sec.SecurityExtension;
import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.PasswordService;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;

import java.util.Optional;

public class PasswordServiceSecurityExtension extends SecurityExtension<PasswordService>
        implements PasswordService
{

    private UserService userService;

    @Override
    public AbstractUser forgotPassword(String contactInformation) throws EntityNotFoundException {
        // anon has to be able to reset password without being logged in
        return getNext().forgotPassword(contactInformation);
    }

    @Override
    public AbstractUser resetPassword(ResetPasswordDto resetPasswordDto) throws EntityNotFoundException, BadEntityException, BadTokenException {
        return getNext().resetPassword(resetPasswordDto);
    }

    @Override
    public AbstractUser changePassword(ChangePasswordDto changePasswordDto) throws EntityNotFoundException, BadEntityException {
        Optional<AbstractUser> user = userService.findByContactInformation(changePasswordDto.getContactInformation());
        if (user.isEmpty())
            throw new EntityNotFoundException("user not found");
        getAclTemplate().checkPermission(user.get(), BasePermission.WRITE);
        return getNext().changePassword(changePasswordDto);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}


