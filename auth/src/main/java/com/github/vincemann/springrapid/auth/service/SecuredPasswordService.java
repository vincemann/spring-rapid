package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.acl.AclTemplate;
import com.github.vincemann.springrapid.auth.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.val.InsufficientPasswordStrengthException;
import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.jwt.BadTokenException;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import com.github.vincemann.springrapid.auth.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;

import static com.github.vincemann.springrapid.auth.util.UserUtils.findPresentByContactInformation;

public class SecuredPasswordService implements PasswordService {

    private PasswordService decorated;
    private AbstractUserRepository userRepository;
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
    public AbstractUser resetPassword(ResetPasswordDto dto) throws EntityNotFoundException, BadEntityException, BadTokenException, InsufficientPasswordStrengthException {
        return decorated.resetPassword(dto);
    }

    @Override
    public AbstractUser changePassword(ChangePasswordDto dto) throws EntityNotFoundException, BadEntityException, InsufficientPasswordStrengthException {
        // fail fast
        VerifyEntity.notEmpty(dto.getContactInformation(),"contact information");
        VerifyEntity.notEmpty(dto.getNewPassword(),"new password");
        VerifyEntity.notEmpty(dto.getOldPassword(),"old password");
        AbstractUser user = findPresentByContactInformation(userRepository,dto.getContactInformation());
        aclTemplate.checkPermission(user, BasePermission.WRITE);
        return decorated.changePassword(dto);
    }

    @Autowired
    public void setUserRepository(AbstractUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setAclTemplate(AclTemplate aclTemplate) {
        this.aclTemplate = aclTemplate;
    }
}
