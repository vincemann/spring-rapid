package com.github.vincemann.springlemon.auth.service.extension;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springlemon.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springlemon.auth.service.SimpleUserService;
import com.github.vincemann.springlemon.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.core.proxy.GenericSimpleCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Map;

@Transactional
public interface GenericSimpleUserServiceExtension<S extends SimpleUserService<U,Id>,U extends AbstractUser<Id>, Id extends Serializable>
        extends SimpleUserService<U,Id>, GenericSimpleCrudServiceExtension<S,U,Id>
{


    @Override
    default Map<String, Object> getSharedProperties(){
        return getNext().getSharedProperties();
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
    default U findByEmail(/*@Valid @Email @NotBlank*/ String email) throws EntityNotFoundException {
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
    default void createAdminUser(LemonProperties.Admin admin) throws BadEntityException {
        getNext().createAdminUser(admin);
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
