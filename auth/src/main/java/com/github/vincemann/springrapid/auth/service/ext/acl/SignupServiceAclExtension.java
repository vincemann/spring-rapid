package com.github.vincemann.springrapid.auth.service.ext.acl;

import com.github.vincemann.springrapid.acl.service.ext.acl.AclExtension;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.SignupService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Transactional
public class SignupServiceAclExtension
        extends AclExtension<SignupService>
            implements SignupService
{


    @Override
    public AbstractUser signup(SignupDto signupDto) throws BadEntityException, AlreadyRegisteredException {
        AbstractUser saved = getNext().signup(signupDto);
        rapidAclService.savePermissionForUserOverEntity(saved.getContactInformation(),saved, BasePermission.ADMINISTRATION);
        return saved;
    }

}
