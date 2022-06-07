package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public interface UserServiceExtension<S extends UserService>
        extends CrudServiceExtension<S>, UserService
{

    @Override
    default Map<String, Object> getContext(){
        return getNext().getContext();
    }

    @Override
    default AbstractUser signup(AbstractUser user) throws BadEntityException, AlreadyRegisteredException {
        return getNext().signup(user);
    }

    @Override
    default void resendVerificationMessage(AbstractUser user) throws EntityNotFoundException, BadEntityException {
        getNext().resendVerificationMessage(user);
    }

    @Override
    default Optional<AbstractUser> findByContactInformation( String contactInformation)  {
        return getNext().findByContactInformation(contactInformation);
    }

    @Override
    default AbstractUser verifyUser(String verificationCode) throws EntityNotFoundException,  BadEntityException {
        return getNext().verifyUser(verificationCode);
    }

    @Override
    default void forgotPassword( String contactInformation) throws EntityNotFoundException {
        getNext().forgotPassword(contactInformation);
    }

    @Override
    default AbstractUser resetPassword(String newPassword, String code) throws EntityNotFoundException, BadEntityException {
        return getNext().resetPassword(newPassword,code);
    }

    @Override
    default void changePassword(AbstractUser user, String oldPassword, String newPassword, String retypeNewPassword) throws EntityNotFoundException, BadEntityException {
        getNext().changePassword(user,oldPassword,newPassword,retypeNewPassword);
    }


    @Override
    default AbstractUser changeContactInformation(/*@NotBlank*/ String changeContactInformationCode) throws EntityNotFoundException, BadEntityException {
        return getNext().changeContactInformation(changeContactInformationCode);
    }

    @Override
    default String createNewAuthToken(String targetUserContactInformation) throws EntityNotFoundException {
        return getNext().createNewAuthToken(targetUserContactInformation);
    }

    @Override
    default String createNewAuthToken() throws EntityNotFoundException {
        return getNext().createNewAuthToken();
    }

    @Override
    default void requestContactInformationChange(AbstractUser user, String newContactInformation) throws EntityNotFoundException, AlreadyRegisteredException {
        getNext().requestContactInformationChange(user,newContactInformation);
    }

    //    @Override
//    default Map<String, String> fetchFullToken(String authHeader) {
//        return getNext().fetchFullToken(authHeader);
//    }

    @Override
    default AbstractUser newAdmin(AuthProperties.Admin admin) {
        return getNext().newAdmin(admin);
    }

    @Override
    default AbstractUser signupAdmin(AbstractUser admin) throws AlreadyRegisteredException, BadEntityException {
        return getNext().signupAdmin(admin);
    }

    @Override
    default Serializable toId(String id) {
        return getNext().toId(id);
    }

//    @Override
//    default void addAuthHeader(HttpServletResponse response, String username, Long expirationMillis) {
//        getNext().addAuthHeader(response,username,expirationMillis);
//    }


    @Override
    default AbstractUser partialUpdate(AbstractUser entity, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        return getNext().partialUpdate(entity,fieldsToRemove);
    }

    @Override
    default AbstractUser fullUpdate(AbstractUser entity) throws BadEntityException, EntityNotFoundException {
        return getNext().fullUpdate(entity);
    }

    @Override
    default AbstractUser softUpdate(AbstractUser entity) throws EntityNotFoundException, BadEntityException {
        return getNext().softUpdate(entity);
    }
}
