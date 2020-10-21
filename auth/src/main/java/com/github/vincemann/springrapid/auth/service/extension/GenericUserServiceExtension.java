package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springrapid.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springrapid.auth.domain.dto.ResetPasswordForm;
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
    default U signup(U user) throws BadEntityException {
        return getNext().signup(user);
    }

    @Override
    default void resendVerificationMail(U user) throws EntityNotFoundException {
        getNext().resendVerificationMail(user);
    }

    @Override
    default Optional<U> findByEmail(/*@Valid @Email @NotBlank*/ String email){
        return getNext().findByEmail(email);
    }

    @Override
    default U verifyUser(U user, String verificationCode) throws EntityNotFoundException, BadTokenException, BadEntityException {
        return getNext().verifyUser(user,verificationCode);
    }

    @Override
    default void forgotPassword(/*@Valid @Email @NotBlank*/ String email) throws EntityNotFoundException {
        getNext().forgotPassword(email);
    }

    @Override
    default U resetPassword(/*@Valid*/ ResetPasswordForm form) throws EntityNotFoundException, BadTokenException {
        return getNext().resetPassword(form);
    }

    @Override
    default void changePassword(U user, /*@Valid*/ ChangePasswordForm changePasswordForm) throws EntityNotFoundException {
        getNext().changePassword(user,changePasswordForm);
    }

    @Override
    default void requestEmailChange(U user, /*@Valid*/ RequestEmailChangeForm emailChangeForm) throws EntityNotFoundException {
        getNext().requestEmailChange(user,emailChangeForm);
    }

    @Override
    default U changeEmail(U user, /*@Valid @NotBlank */String changeEmailCode) throws EntityNotFoundException, BadTokenException {
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
    default U createAdminUser(AuthProperties.Admin admin) throws BadEntityException {
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
