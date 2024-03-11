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

/**
 * Default generic decorator for securing {@link UserService}s.
 * Extend this class for creating own user service security decorator.
 * Default impl: {@link SecuredUserService}.
 *
 * @param <S>  decorated user service (service to secure)
 * @param <U>  user entity type
 * @param <Id> id type of user
 */
public abstract class AbstractSecuredUserServiceDecorator
        <
                S extends UserService<U, Id>,
                U extends AbstractUser<Id>,
                Id extends Serializable
                >
        extends SecuredCrudServiceDecorator<S, U, Id>
        implements UserService<U, Id> {

    public AbstractSecuredUserServiceDecorator(S decorated) {
        super(decorated);
    }

    @Override
    public U create(U entity) throws BadEntityException {
        AuthorizationTemplate.assertHasRoles(AuthRoles.ADMIN);
        return getDecorated().create(entity);
    }

    @Transactional
    @Override
    public Optional<U> findByContactInformation(String contactInformation) {
        Optional<U> user = getDecorated().findByContactInformation(contactInformation);
        user.ifPresent(usr -> getAclTemplate().checkPermission(usr, BasePermission.READ));
        return getDecorated().findByContactInformation(contactInformation);
    }

    @Transactional
    @Override
    public U findPresentByContactInformation(String contactInformation) throws EntityNotFoundException {
        U user = getDecorated().findPresentByContactInformation(contactInformation);
        getAclTemplate().checkPermission(user, BasePermission.READ);
        return user;
    }

    @Override
    public U addRole(Id userId, String role) throws EntityNotFoundException, BadEntityException {
        AuthorizationTemplate.assertHasRoles(AuthRoles.ADMIN);
        return getDecorated().addRole(userId, role);
    }

    @Override
    public U removeRole(Id userId, String role) throws EntityNotFoundException, BadEntityException {
        AuthorizationTemplate.assertHasRoles(AuthRoles.ADMIN);
        return getDecorated().removeRole(userId, role);
    }

    @Override
    public U updatePassword(Id userId, String password) throws EntityNotFoundException, BadEntityException {
        getAclTemplate().checkPermission(userId, getEntityClass(), BasePermission.WRITE);
        return getDecorated().updatePassword(userId, password);
    }

    @Override
    public U updateContactInformation(Id userId, String contactInformation) throws EntityNotFoundException, BadEntityException {
        getAclTemplate().checkPermission(userId, getEntityClass(), BasePermission.WRITE);
        return getDecorated().updateContactInformation(userId, contactInformation);
    }

    @Override
    public U createUser() {
        throw new UnsupportedOperationException("for internal use only");
    }

    @Override
    public U createAdmin(AuthProperties.Admin admin) {
        throw new UnsupportedOperationException("for internal use only");
    }

    @Override
    public U blockUser(String contactInformation) throws EntityNotFoundException, BadEntityException {
        AuthorizationTemplate.assertHasRoles(AuthRoles.ADMIN);
        return getDecorated().blockUser(contactInformation);
    }
}
