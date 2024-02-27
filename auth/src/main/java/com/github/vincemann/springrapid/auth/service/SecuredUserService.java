package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.acl.service.SecuredCrudServiceDecorator;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.core.sec.AuthorizationTemplate;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

public class SecuredUserService
        // looks ugly but is just UserService in raw form
        extends SecuredCrudServiceDecorator<UserService<AbstractUser<Serializable>,Serializable>, AbstractUser<Serializable>, Serializable>
        implements UserService<AbstractUser<Serializable>,Serializable>
{


    public SecuredUserService(UserService<AbstractUser<Serializable>, Serializable> decorated) {
        super(decorated);
    }

    @Override
    public AbstractUser<Serializable> create(AbstractUser<Serializable> entity) throws BadEntityException {
        AuthorizationTemplate.assertHasRoles(AuthRoles.ADMIN);
        return getDecorated().create(entity);
    }

    @Transactional
    @Override
    public Optional<AbstractUser<Serializable>> findByContactInformation(String contactInformation) {
        Optional<AbstractUser<Serializable>> user = getDecorated().findByContactInformation(contactInformation);
        user.ifPresent(usr -> getAclTemplate().checkPermission(usr, BasePermission.READ));
        return getDecorated().findByContactInformation(contactInformation);
    }

    @Transactional
    @Override
    public AbstractUser<Serializable> findPresentByContactInformation(String contactInformation) throws EntityNotFoundException {
        AbstractUser<Serializable> user = getDecorated().findPresentByContactInformation(contactInformation);
        getAclTemplate().checkPermission(user, BasePermission.READ);
        return user;
    }

    @Override
    public AbstractUser<Serializable> addRole(Serializable userId, String role) throws EntityNotFoundException, BadEntityException {
        AuthorizationTemplate.assertHasRoles(AuthRoles.ADMIN);
        return getDecorated().addRole(userId,role);
    }

    @Override
    public AbstractUser<Serializable> removeRole(Serializable userId, String role) throws EntityNotFoundException, BadEntityException {
        AuthorizationTemplate.assertHasRoles(AuthRoles.ADMIN);
        return getDecorated().removeRole(userId,role);
    }

    @Override
    public AbstractUser<Serializable> updatePassword(Serializable userId, String password) throws EntityNotFoundException, BadEntityException {
        getAclTemplate().checkPermission(userId,getEntityClass(), BasePermission.WRITE);
        return getDecorated().updatePassword(userId,password);
    }

    @Override
    public AbstractUser<Serializable> updateContactInformation(Serializable userId, String contactInformation) throws EntityNotFoundException, BadEntityException {
        getAclTemplate().checkPermission(userId, getEntityClass(), BasePermission.WRITE);
        return getDecorated().updateContactInformation(userId,contactInformation);
    }


    @Override
    public AbstractUser<Serializable> createUser() {
        throw new IllegalArgumentException("for internal use only");
    }

    @Override
    public AbstractUser<Serializable> createAdmin(AuthProperties.Admin admin) {
        throw new IllegalArgumentException("for internal use only");
    }

    @Override
    public AbstractUser<Serializable> blockUser(String contactInformation) throws EntityNotFoundException, BadEntityException {
        AuthorizationTemplate.assertHasRoles(AuthRoles.ADMIN);
        return getDecorated().blockUser(contactInformation);
    }
}
