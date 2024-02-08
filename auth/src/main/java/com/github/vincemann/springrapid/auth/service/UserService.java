package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Validated
public interface UserService<U extends AbstractUser<ID>, ID extends Serializable>
        extends CrudService<U,ID>, AopLoggable
{

    public Optional<U> findByContactInformation( @NotBlank String contactInformation);

    U addRole(ID userId, String role) throws EntityNotFoundException, BadEntityException;

    U removeRole(ID userId, String role) throws EntityNotFoundException, BadEntityException;

    void updatePassword(ID userId, String password) throws EntityNotFoundException, BadEntityException;

    U updateContactInformation(ID userId, String contactInformation) throws EntityNotFoundException, BadEntityException;

    U updateContactInformation(U update, String contactInformation) throws EntityNotFoundException, BadEntityException;


    // keep it like that, otherwise the AbstractUser type wont be in impl methods
    @Override
    U partialUpdate(U entity, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException;

    @Override
    U fullUpdate(U entity) throws BadEntityException, EntityNotFoundException;

    @Override
    U softUpdate(U entity) throws EntityNotFoundException, BadEntityException;

    U createUser();

    U createAdmin(@Valid AuthProperties.Admin admin);

}
