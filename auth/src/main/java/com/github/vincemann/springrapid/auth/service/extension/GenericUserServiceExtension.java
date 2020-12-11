package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springrapid.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springrapid.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Transactional
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
    default void resendVerificationMail(U user) throws EntityNotFoundException, BadEntityException {
        getNext().resendVerificationMail(user);
    }

    @Override
    default Optional<U> findByEmail( String email){
        return getNext().findByEmail(email);
    }

    @Override
    default U verifyUser(U user, String verificationCode) throws EntityNotFoundException,  BadEntityException {
        return getNext().verifyUser(user,verificationCode);
    }

    @Override
    default void forgotPassword( String email) throws EntityNotFoundException {
        getNext().forgotPassword(email);
    }

    @Override
    default U resetPassword( ResetPasswordForm form) throws EntityNotFoundException, BadEntityException {
        return getNext().resetPassword(form);
    }

    @Override
    default void changePassword(U user,  ChangePasswordForm changePasswordForm) throws EntityNotFoundException, BadEntityException {
        getNext().changePassword(user,changePasswordForm);
    }

    @Override
    default void requestEmailChange(U user,  RequestEmailChangeForm emailChangeForm) throws EntityNotFoundException, AlreadyRegisteredException {
        getNext().requestEmailChange(user,emailChangeForm);
    }

    @Override
    default U changeEmail(U user,String changeEmailCode) throws EntityNotFoundException, BadEntityException {
        return getNext().changeEmail(user,changeEmailCode);
    }

    @Override
    default String createNewAuthToken(String targetUserEmail){
        return getNext().createNewAuthToken(targetUserEmail);
    }

    @Override
    default String createNewAuthToken(){
        return getNext().createNewAuthToken();
    }

    @Override
    default U createAdminUser(AuthProperties.Admin admin) throws BadEntityException, AlreadyRegisteredException {
        return getNext().createAdminUser(admin);
    }

    @Override
    default Id toId(String id) {
        return getNext().toId(id);
    }


    @Override
    default U update(U entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        return getNext().update(entity,full);
    }
}
