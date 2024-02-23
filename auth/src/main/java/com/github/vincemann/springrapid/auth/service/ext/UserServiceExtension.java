package com.github.vincemann.springrapid.auth.service.ext;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.proxy.NextLinkAware;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.io.Serializable;
import java.util.Optional;

public interface UserServiceExtension extends NextLinkAware<UserService>, UserService, CrudServiceExtension<UserService> {

    @Override
    default Optional findByContactInformation(String contactInformation) {
        return getNext().findByContactInformation(contactInformation);
    }

    @Override
    default AbstractUser addRole(Serializable userId, String role) throws EntityNotFoundException, BadEntityException {
        return getNext().addRole(userId,role);
    }

    @Override
    default AbstractUser removeRole(Serializable userId, String role) throws EntityNotFoundException, BadEntityException {
        return getNext().removeRole(userId,role);
    }

    @Override
    default AbstractUser updatePassword(Serializable userId, String password) throws EntityNotFoundException, BadEntityException {
        return getNext().updatePassword(userId,password);
    }

    @Override
    default AbstractUser findPresentByContactInformation(String contactInformation) throws EntityNotFoundException {
        return getNext().findPresentByContactInformation(contactInformation);
    }

    @Override
    default AbstractUser blockUser(String contactInformation) throws EntityNotFoundException, BadEntityException {
        return getNext().blockUser(contactInformation);
    }

    @Override
    default AbstractUser updateContactInformation(Serializable userId, String contactInformation) throws EntityNotFoundException, BadEntityException {
        return getNext().updateContactInformation(userId,contactInformation);
    }

    @Override
    default AbstractUser createUser() {
        return getNext().createUser();
    }

    @Override
    default AbstractUser createAdmin(AuthProperties.Admin admin) {
        return getNext().createAdmin(admin);
    }
}
