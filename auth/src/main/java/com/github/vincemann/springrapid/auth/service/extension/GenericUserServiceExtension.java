package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public interface GenericUserServiceExtension<S extends UserService<U,Id>,U extends AbstractUser<Id>, Id extends Serializable>
        extends UserService<U,Id>, GenericCrudServiceExtension<S,U,Id>
{


    @Override
    default Map<String, Object> getContext(){
        return getNext().getContext();
    }

    @Override
    default U signup(U user) throws BadEntityException, AlreadyRegisteredException {
        return getNext().signup(user);
    }

    @Override
    default U signupAdmin(U admin) throws AlreadyRegisteredException, BadEntityException{
        return getNext().signupAdmin(admin);
    }

    @Override
    default void resendVerificationMessage(U user) throws EntityNotFoundException, BadEntityException {
        getNext().resendVerificationMessage(user);
    }

    @Override
    default Optional<U> findByContactInformation( String contactInformation){
        return getNext().findByContactInformation(contactInformation);
    }

    @Override
    default U verifyUser(String verificationCode) throws EntityNotFoundException,  BadEntityException {
        return getNext().verifyUser(verificationCode);
    }

    @Override
    default void forgotPassword( String contactInformation) throws EntityNotFoundException {
        getNext().forgotPassword(contactInformation);
    }

    @Override
    default U resetPassword(String newPassword, String code) throws EntityNotFoundException, BadEntityException {
        return getNext().resetPassword(newPassword,code);
    }

    @Override
    default void changePassword(U user, String oldPassword, String newPassword, String retypeNewPassword) throws EntityNotFoundException, BadEntityException {
        getNext().changePassword(user,oldPassword,newPassword,retypeNewPassword);
    }

    @Override
    default void requestContactInformationChange(U user, String newContactInformation) throws EntityNotFoundException, AlreadyRegisteredException, BadEntityException {
        getNext().requestContactInformationChange(user,newContactInformation);
    }

    @Override
    default U changeContactInformation(String changeContactInformationCode) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException {
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
    default U newAdmin(AuthProperties.Admin admin) {
        return getNext().newAdmin(admin);
    }

//    @Override
//    default Id toId(String id) {
//        return getNext().toId(id);
//    }

    @Override
    default U partialUpdate(U entity, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException {
        return getNext().partialUpdate(entity, fieldsToUpdate);
    }

    @Override
    default U fullUpdate(U entity) throws BadEntityException, EntityNotFoundException {
        return getNext().fullUpdate(entity);
    }

    @Override
    default U softUpdate(U entity) throws EntityNotFoundException, BadEntityException {
        return getNext().softUpdate(entity);
    }
}
