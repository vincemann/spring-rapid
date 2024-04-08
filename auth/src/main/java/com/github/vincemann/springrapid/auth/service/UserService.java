package com.github.vincemann.springrapid.auth.service;


import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.val.InsufficientPasswordStrengthException;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;

import java.io.Serializable;
import java.util.Optional;

public interface UserService<U extends AbstractUser<ID>, ID extends Serializable>
{

    Class<U> getEntityClass();
    U create( U user) throws BadEntityException, InsufficientPasswordStrengthException;
    Optional<U> findByContactInformation(String contactInformation);

    U addRole(ID userId, String role) throws EntityNotFoundException, BadEntityException;

    U removeRole(ID userId, String role) throws EntityNotFoundException, BadEntityException;

    U updatePassword( ID userId, String password) throws EntityNotFoundException, BadEntityException, InsufficientPasswordStrengthException;

    U updateContactInformation( ID userId, String contactInformation) throws EntityNotFoundException, BadEntityException;

    U createUser();

    U createAdmin(AuthProperties.Admin admin);

    U blockUser(String contactInformation) throws EntityNotFoundException, BadEntityException;

    void delete(ID id) throws EntityNotFoundException;
}
