package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;

import com.github.vincemann.springrapid.acl.service.ext.acl.AclExtension;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.sec.Roles;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Transactional
public class AclUserExtension
        extends AclExtension<UserService>
            implements UserServiceExtension<UserService>
{

    @LogInteraction
    @Override
    public IdentifiableEntity create(IdentifiableEntity entity) throws BadEntityException {
        AbstractUser saved = (AbstractUser) getNext().create(entity);
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
    public AbstractUser signupAdmin(AbstractUser admin) throws AlreadyRegisteredException, BadEntityException {
        AbstractUser saved = getNext().signupAdmin(admin);
        savePostSignupAclInfo(saved);
        return saved;
    }

    public void savePostSignupAclInfo(AbstractUser saved){
        rapidAclService.savePermissionForUserOverEntity(saved.getContactInformation(),saved, BasePermission.ADMINISTRATION);
        if (!saved.getRoles().contains(AuthRoles.ADMIN)) {
            rapidAclService.savePermissionForRoleOverEntity(saved, Roles.ADMIN, BasePermission.ADMINISTRATION);
        }else {
            // admins can only read other admins
            rapidAclService.savePermissionForRoleOverEntity(saved,AuthRoles.ADMIN, BasePermission.READ);
        }
    }

}
