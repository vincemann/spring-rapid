package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Optional;

// only do validation on highest level, that is exposed to user, not when only using internally
public interface UserService<U extends AbstractUser<ID>, ID extends Serializable>
        extends CrudService<U,ID>
{

    public Optional<U> findByContactInformation(String contactInformation);
    public U findPresentByContactInformation(String contactInformation) throws EntityNotFoundException;

    U addRole(ID userId, String role) throws EntityNotFoundException, BadEntityException;

    U removeRole(ID userId, String role) throws EntityNotFoundException, BadEntityException;

    U updatePassword(ID userId, String password) throws EntityNotFoundException, BadEntityException;

    U updateContactInformation(ID userId, String contactInformation) throws EntityNotFoundException, BadEntityException;


    // keep it like that, otherwise the AbstractUser type wont be in impl methods
    @Override
    U partialUpdate(U update, String... fieldsToUpdate) throws EntityNotFoundException;

    @Override
    U fullUpdate(U update) throws EntityNotFoundException;

    @Override
    U softUpdate(U entity) throws EntityNotFoundException;

    U createUser();

    U createAdmin(AuthProperties.Admin admin);

    U blockUser(String contactInformation) throws EntityNotFoundException, BadEntityException;
}
