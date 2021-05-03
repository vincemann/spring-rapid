package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.AuthRoles;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;

import com.github.vincemann.springrapid.acl.service.extensions.AbstractAclExtension;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Transactional
public class AclUserExtension
        extends AbstractAclExtension<UserService>
            implements UserServiceExtension<UserService>
{


    @LogInteraction
    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        AbstractUser saved = (AbstractUser) getNext().save(entity);
        savePostSignupAclInfo(saved);
        return saved;
    }

    @LogInteraction
    @Override
    public AbstractUser signup(AbstractUser user) throws BadEntityException, AlreadyRegisteredException {
        AbstractUser saved = getNext().signup(user);
        savePostSignupAclInfo(saved);
        return saved;
    }

    @LogInteraction
    @Override
    public AbstractUser createAdminUser(AuthProperties.Admin admin) throws BadEntityException, AlreadyRegisteredException {
        AbstractUser saved = getNext().createAdminUser(admin);
        savePostSignupAclInfo(saved);
        return saved;
    }


    public void savePostSignupAclInfo(AbstractUser saved){
        savePermissionForUserOverEntity(saved.getEmail(),saved, BasePermission.ADMINISTRATION);
        if (!saved.getRoles().contains(AuthRoles.ADMIN)) {
            saveFullPermissionForAdminOverEntity(saved);
        }else {
            // admins can only read other admins
            savePermissionForUserOverEntity(AuthRoles.ADMIN,saved, BasePermission.READ);
        }
    }

}
