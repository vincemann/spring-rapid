package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.acl.AclTemplate;
import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;

import java.util.Optional;

public class SecuredPasswordService implements PasswordService {

    private PasswordService decorated;
    private UserService userService;
    private AclTemplate aclTemplate;

    public SecuredPasswordService(PasswordService decorated) {
        this.decorated = decorated;
    }

    @Override
    public AbstractUser forgotPassword(String contactInformation) throws EntityNotFoundException, BadEntityException {
        // anon has to be able to reset password without being logged in
        return decorated.forgotPassword(contactInformation);
    }

    @Override
    public AbstractUser resetPassword(ResetPasswordDto dto) throws EntityNotFoundException, BadEntityException, BadTokenException {
        return decorated.resetPassword(dto);
    }

    @Override
    public AbstractUser changePassword(ChangePasswordDto dto) throws EntityNotFoundException, BadEntityException {
        Optional<AbstractUser> user = userService.findByContactInformation(dto.getContactInformation());
        VerifyEntity.isPresent(user,"user not found");
        aclTemplate.checkPermission(user.get(), BasePermission.WRITE);
        return decorated.changePassword(dto);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAclTemplate(AclTemplate aclTemplate) {
        this.aclTemplate = aclTemplate;
    }
}
