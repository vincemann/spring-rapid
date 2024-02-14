package com.github.vincemann.springrapid.auth.service.ext.sec;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.acl.service.ext.sec.SecurityExtension;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.sec.AuthorizationTemplate;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;


@Slf4j
public class UserServiceSecurityExtension
        extends SecurityExtension<UserService>
            implements UserService, CrudServiceExtension<UserService>
{

    @LogInteraction
    @Override
    public IdentifiableEntity create(IdentifiableEntity entity) throws BadEntityException {
        AuthorizationTemplate.assertHasRoles(AuthRoles.ADMIN);
        return getNext().create(entity);
    }

    @Override
    public AbstractUser addRole(Serializable userId, String role) throws EntityNotFoundException, BadEntityException {
        AuthorizationTemplate.assertHasRoles(AuthRoles.ADMIN);
        return getNext().addRole(userId,role);
    }

    @Override
    public AbstractUser removeRole(Serializable userId, String role) throws EntityNotFoundException, BadEntityException {
        AuthorizationTemplate.assertHasRoles(AuthRoles.ADMIN);
        return getNext().removeRole(userId,role);
    }

    @Override
    public AbstractUser updatePassword(Serializable userId, String password) throws EntityNotFoundException, BadEntityException {
        getAclTemplate().checkPermission(userId,getLast().getEntityClass(), BasePermission.WRITE);
        return getNext().updatePassword(userId,password);
    }

    @Override
    public AbstractUser updateContactInformation(Serializable userId, String contactInformation) throws EntityNotFoundException, BadEntityException {
        getAclTemplate().checkPermission(userId,getLast().getEntityClass(), BasePermission.WRITE);
        return getNext().updateContactInformation(userId,contactInformation);
    }

    @Override
    public AbstractUser updateContactInformation(AbstractUser update, String contactInformation) throws EntityNotFoundException, BadEntityException {
        getAclTemplate().checkPermission(update, BasePermission.WRITE);
        return getNext().updateContactInformation(update,contactInformation);
    }

    @Override
    public AbstractUser createUser() {
        return getNext().createUser();
    }

    @LogInteraction
    @Override
    public AbstractUser fullUpdate(AbstractUser entity) throws BadEntityException, EntityNotFoundException {
        getAclTemplate().checkPermission(entity, BasePermission.WRITE);
        return getNext().fullUpdate(entity);
    }

    @Override
    public AbstractUser softUpdate(AbstractUser entity) throws EntityNotFoundException, BadEntityException {
        getAclTemplate().checkPermission(entity, BasePermission.WRITE);
        return getNext().softUpdate(entity);
    }

    @LogInteraction
    @Override
    public AbstractUser partialUpdate(AbstractUser entity, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException {
        getAclTemplate().checkPermission(entity, BasePermission.WRITE);
        return getNext().partialUpdate(entity, fieldsToUpdate);
    }


    @Override
    public Optional findByContactInformation(String contactInformation) {
        Optional<AbstractUser> user = getLast().findByContactInformation(contactInformation);
        if (user.isPresent())
            getAclTemplate().checkPermission(user.get(), BasePermission.READ);
        return getNext().findByContactInformation(contactInformation);
    }

    @Override
    public AbstractUser createAdmin(AuthProperties.Admin admin) {
        return getNext().createAdmin(admin);
    }

}
