package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.RequestEmailChangeDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

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
    default void resendVerificationMail(U user) throws EntityNotFoundException, BadEntityException {
        getNext().resendVerificationMail(user);
    }

    @Override
    default Optional<U> findByEmail( String email){
        return getNext().findByEmail(email);
    }

    @Override
    default U verifyUser(String verificationCode) throws EntityNotFoundException,  BadEntityException {
        return getNext().verifyUser(verificationCode);
    }

    @Override
    default void forgotPassword( String email) throws EntityNotFoundException {
        getNext().forgotPassword(email);
    }

    @Override
    default U resetPassword( ResetPasswordDto dto,String code) throws EntityNotFoundException, BadEntityException {
        return getNext().resetPassword(dto, code);
    }

    @Override
    default void changePassword(U user,  ChangePasswordDto changePasswordForm) throws EntityNotFoundException, BadEntityException {
        getNext().changePassword(user,changePasswordForm);
    }

    @Override
    default void requestEmailChange(U user,  RequestEmailChangeDto emailChangeForm) throws EntityNotFoundException, AlreadyRegisteredException {
        getNext().requestEmailChange(user,emailChangeForm);
    }

    @Override
    default U changeEmail(String changeEmailCode) throws EntityNotFoundException, BadEntityException {
        return getNext().changeEmail(changeEmailCode);
    }

    @Override
    default String createNewAuthToken(String targetUserEmail) throws EntityNotFoundException {
        return getNext().createNewAuthToken(targetUserEmail);
    }

    @Override
    default String createNewAuthToken() throws EntityNotFoundException {
        return getNext().createNewAuthToken();
    }

    @Override
    default U newAdmin(AuthProperties.Admin admin) {
        return getNext().newAdmin(admin);
    }

    @Override
    default Id toId(String id) {
        return getNext().toId(id);
    }

    @Override
    default U partialUpdate(U entity, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        return getNext().partialUpdate(entity,fieldsToRemove);
    }

    @Override
    default U update(U entity) throws BadEntityException, EntityNotFoundException {
        return getNext().update(entity);
    }
}
