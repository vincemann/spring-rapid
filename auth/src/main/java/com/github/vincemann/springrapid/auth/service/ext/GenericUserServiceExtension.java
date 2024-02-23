package com.github.vincemann.springrapid.auth.service.ext;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.io.Serializable;
import java.util.Optional;

public interface GenericUserServiceExtension<S extends UserService<U, Id>, U extends AbstractUser<Id>, Id extends Serializable>
    extends GenericCrudServiceExtension<S, U, Id>, UserService<U, Id> {
    @Override
    default Optional<U> findByContactInformation(String contactInformation) {
        return getNext().findByContactInformation(contactInformation);
    }

    @Override
    default U addRole(Id userId, String role) throws EntityNotFoundException, BadEntityException {
        return getNext().addRole(userId, role);
    }

    @Override
    default U removeRole(Id userId, String role) throws EntityNotFoundException, BadEntityException {
        return getNext().removeRole(userId, role);
    }

    @Override
    default U updatePassword(Id userId, String password) throws EntityNotFoundException, BadEntityException {
        return getNext().updatePassword(userId, password);
    }

    @Override
    default U softUpdate(U entity) throws EntityNotFoundException, BadEntityException {
        return GenericCrudServiceExtension.super.softUpdate(entity);
    }

    @Override
    default U partialUpdate(U entity, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException {
        return GenericCrudServiceExtension.super.partialUpdate(entity, fieldsToUpdate);
    }

    @Override
    default U findPresentByContactInformation(String contactInformation) throws EntityNotFoundException {
        return getNext().findPresentByContactInformation(contactInformation);
    }

    @Override
    default U blockUser(String contactInformation) throws EntityNotFoundException, BadEntityException {
        return getNext().blockUser(contactInformation);
    }

    @Override
    default U findPresentById(Id id) throws EntityNotFoundException {
        return getNext().findPresentById(id);
    }

    @Override
    default U fullUpdate(U entity) throws BadEntityException, EntityNotFoundException {
        return GenericCrudServiceExtension.super.fullUpdate(entity);
    }

    @Override
    default U updateContactInformation(Id userId, String contactInformation) throws EntityNotFoundException, BadEntityException {
        return getNext().updateContactInformation(userId, contactInformation);
    }

    @Override
    default U createUser() {
        return getNext().createUser();
    }

    @Override
    default U createAdmin(AuthProperties.Admin admin) {
        return getNext().createAdmin(admin);
    }
}
