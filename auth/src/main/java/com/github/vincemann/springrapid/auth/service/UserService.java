package com.github.vincemann.springrapid.auth.service;


import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.Optional;

public interface UserService<U extends AbstractUser<ID>, ID extends Serializable>
{

    Class<U> getEntityClass();
    U create( U user) throws BadEntityException;
    Optional<U> findByContactInformation(String contactInformation);

    U addRole(ID userId, String role) throws EntityNotFoundException, BadEntityException;

    U removeRole(ID userId, String role) throws EntityNotFoundException, BadEntityException;

    U updatePassword( ID userId, String password) throws EntityNotFoundException, BadEntityException;

    U updateContactInformation( ID userId, String contactInformation) throws EntityNotFoundException, BadEntityException;

    U createUser();

    U createAdmin(AuthProperties.Admin admin);

    U blockUser(String contactInformation) throws EntityNotFoundException, BadEntityException;

    void delete(ID id) throws EntityNotFoundException;
}
