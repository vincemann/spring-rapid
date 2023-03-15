package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Validated
@ServiceComponent
public interface UserService<U extends AbstractUser<ID>, ID extends Serializable>
        extends AopLoggable, CrudService<U,ID>
{

    public Map<String, Object> getContext();

//    @Validated(UserVerifyUtils.SignUpValidation.class)
    // todo do captcha validation programatically in service witch mockable captcha validator for tests
    public U signup( U user) throws BadEntityException, AlreadyRegisteredException;

    public void resendVerificationMessage(U user) throws EntityNotFoundException, BadEntityException;

    @LogInteraction(Severity.TRACE)
    public Optional<U> findByContactInformation( @NotBlank String contactInformation);

    // get user from contactInformation from code
    public U verifyUser(/*U user,*/@NotBlank String verificationCode) throws EntityNotFoundException,  BadEntityException;
    public void forgotPassword( @NotBlank String contactInformation) throws EntityNotFoundException;

    // use newPassword here so https encrypts new password which is not possible via url param
    // target contactInformation gets extracted from code
    public U resetPassword(String newPassword, String code) throws EntityNotFoundException,  BadEntityException;
    public void changePassword(U user, String oldPassword, String newPassword, String retypeNewPassword) throws EntityNotFoundException, BadEntityException;

    // get user from contactInformation from code
    public U changeContactInformation(/*U user,*/  @NotBlank String changeContactInformationCode) throws EntityNotFoundException, BadEntityException;

    public void requestContactInformationChange(U user, String newContactInformation) throws EntityNotFoundException, AlreadyRegisteredException;


    @LogInteraction(Severity.TRACE)
    public String createNewAuthToken(String targetUserContactInformation) throws EntityNotFoundException;

    public String createNewAuthToken() throws EntityNotFoundException;

//    @LogInteraction(Severity.TRACE)
//    public Map<String, String> fetchFullToken(String authHeader);

    public U newAdmin(AuthProperties.Admin admin);

    public U signupAdmin(U admin) throws AlreadyRegisteredException, BadEntityException;

//    @LogInteraction(Severity.TRACE)
//    public abstract ID toId(String id);

//    @Validated(UserVerifyUtils.UpdateValidation.class)


    // keep it like that, otherwise the AbstractUser type wont be in impl methods
    @Override
    U partialUpdate(U entity, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException;

    U partialUpdate(U entity, Set<String> propertiesToUpdate, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException;

    @Override
    U fullUpdate(U entity) throws BadEntityException, EntityNotFoundException;

    @Override
    U softUpdate(U entity) throws EntityNotFoundException, BadEntityException;
}
