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
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Validated
public interface UserService<U extends AbstractUser<ID>, ID extends Serializable>
        extends CrudService<U,ID>, AopLoggable
{

    public Map<String, Object> getContext();



    public void resendVerificationMessage(U user) throws EntityNotFoundException, BadEntityException;

    @LogInteraction(Severity.TRACE)
    public Optional<U> findByContactInformation( @NotBlank String contactInformation);


    // get user from contactInformation from code
    public U verifyUser(@NotBlank String verificationCode) throws EntityNotFoundException, BadEntityException, BadTokenException;

    public void forgotPassword(@NotBlank String contactInformation) throws EntityNotFoundException;

    // use newPassword here so https encrypts new password which is not possible via url param
    // target contactInformation gets extracted from code
    public U resetPassword(@NotBlank String newPassword,@NotBlank String code) throws EntityNotFoundException, BadEntityException, BadTokenException;

    void addRole(ID userId, String role) throws EntityNotFoundException, BadEntityException;

    void removeRole(ID userId, String role) throws EntityNotFoundException, BadEntityException;

    void updatePassword(ID userId, String password) throws EntityNotFoundException, BadEntityException;

    public void changePassword(U user, @NotBlank String oldPassword, @NotBlank String newPassword, @NotBlank String retypeNewPassword) throws EntityNotFoundException, BadEntityException;

    // get user from contactInformation from code
    public U changeContactInformation(@NotBlank String changeContactInformationCode) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException;

    public void requestContactInformationChange(U user,@NotBlank String newContactInformation) throws EntityNotFoundException, AlreadyRegisteredException, BadEntityException;


    @LogInteraction(Severity.TRACE)
    public String createNewAuthToken(@NotBlank String targetUserContactInformation) throws EntityNotFoundException;

    public String createNewAuthToken() throws EntityNotFoundException;

//    @LogInteraction(Severity.TRACE)
//    public Map<String, String> fetchFullToken(String authHeader);

    public U newAdmin(@Valid AuthProperties.Admin admin);

    public U signupAdmin(U admin) throws AlreadyRegisteredException, BadEntityException;



    // keep it like that, otherwise the AbstractUser type wont be in impl methods
    @Override
    U partialUpdate(U entity, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException;

    @Override
    U fullUpdate(U entity) throws BadEntityException, EntityNotFoundException;

    @Override
    U softUpdate(U entity) throws EntityNotFoundException, BadEntityException;

    U createUser();

}
