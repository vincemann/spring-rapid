package com.github.vincemann.springrapid.acl.service.sec;

import com.github.vincemann.springrapid.auth.Roles;
import com.github.vincemann.springrapid.auth.*;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.val.InsufficientPasswordStrengthException;
import com.github.vincemann.springrapid.acl.util.AuthorizationUtils;
import org.springframework.core.GenericTypeResolver;
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
public abstract class SecuredUserServiceDecorator
        <
                S extends UserService<U, Id>,
                U extends AbstractUser<Id>,
                Id extends Serializable
                >
        extends SecuredServiceDecorator<S>
        implements UserService<U, Id> {

    private Class<U> entityClass;
    public SecuredUserServiceDecorator(S decorated) {
        super(decorated);
        this.entityClass = (Class<U>) GenericTypeResolver.resolveTypeArguments(this.getClass(), SecuredUserServiceDecorator.class)[1];
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<U> findByContactInformation(String contactInformation) {
        Optional<U> user = getDecorated().findByContactInformation(contactInformation);
        user.ifPresent(usr -> getAclTemplate().checkPermission(usr, BasePermission.READ));
        return getDecorated().findByContactInformation(contactInformation);
    }

    @Transactional
    @Override
    public U create(U user) throws BadEntityException, InsufficientPasswordStrengthException {
        AuthorizationUtils.assertHasRoles(Roles.ADMIN);
        return getDecorated().create(user);
    }

    @Transactional
    @Override
    public void delete(Id id) throws EntityNotFoundException {
        getAclTemplate().checkPermission(id,getEntityClass(), BasePermission.DELETE);
        getDecorated().delete(id);
    }

    @Transactional
    @Override
    public U addRole(Id userId, String role) throws EntityNotFoundException, BadEntityException {
        AuthorizationUtils.assertHasRoles(Roles.ADMIN);
        return getDecorated().addRole(userId, role);
    }

    @Transactional
    @Override
    public U removeRole(Id userId, String role) throws EntityNotFoundException, BadEntityException {
        AuthorizationUtils.assertHasRoles(Roles.ADMIN);
        return getDecorated().removeRole(userId, role);
    }

    @Transactional
    @Override
    public U updatePassword(Id userId, String password) throws EntityNotFoundException, BadEntityException {
        getAclTemplate().checkPermission(userId, getEntityClass(), BasePermission.WRITE);
        return getDecorated().updatePassword(userId, password);
    }

    @Transactional
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

    @Transactional
    @Override
    public U blockUser(String contactInformation) throws EntityNotFoundException, BadEntityException {
        AuthorizationUtils.assertHasRoles(Roles.ADMIN);
        return getDecorated().blockUser(contactInformation);
    }

    public Class<U> getEntityClass() {
        return entityClass;
    }
}
