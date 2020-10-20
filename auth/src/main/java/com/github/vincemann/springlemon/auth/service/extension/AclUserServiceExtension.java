package com.github.vincemann.springlemon.auth.service.extension;

import com.github.vincemann.springlemon.auth.AuthProperties;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AuthRoles;
import com.github.vincemann.springlemon.auth.service.UserService;

import com.github.vincemann.springrapid.acl.service.extensions.AbstractAclServiceExtension;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Transactional
public class AclUserServiceExtension
        extends AbstractAclServiceExtension<UserService>
            implements UserServiceExtension<UserService>
{



    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        AbstractUser saved = (AbstractUser) getNext().save(entity);
        savePostSignupAclInfo(saved);
        return saved;
    }


    @Override
    public AbstractUser signup(AbstractUser user) throws BadEntityException {
        AbstractUser saved = getNext().signup(user);
        savePostSignupAclInfo(user);
        return saved;
    }

    @Override
    public AbstractUser createAdminUser(AuthProperties.Admin admin) throws BadEntityException {
        AbstractUser saved = getNext().createAdminUser(admin);
        savePostSignupAclInfo(saved);
        return saved;
    }


    public void savePostSignupAclInfo(AbstractUser saved){
        savePermissionForUserOver(saved.getEmail(),saved, BasePermission.ADMINISTRATION);
        if (!saved.getRoles().contains(AuthRoles.ADMIN)) {
            saveFullPermissionForAdminOver(saved);
        }else {
            savePermissionForUserOver(AuthRoles.ADMIN,saved, BasePermission.READ);
        }
    }

}
