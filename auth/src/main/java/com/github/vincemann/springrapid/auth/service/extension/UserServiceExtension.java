package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springrapid.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springrapid.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
    default void resendVerificationMail(AbstractUser user) throws EntityNotFoundException, BadEntityException {
        getNext().resendVerificationMail(user);
    }

    @Override
    default Optional<AbstractUser> findByEmail( String email)  {
        return getNext().findByEmail(email);
    }

    @Override
    default AbstractUser verifyUser(AbstractUser user, String verificationCode) throws EntityNotFoundException,  BadEntityException {
        return getNext().verifyUser(user,verificationCode);
    }

    @Override
    default void forgotPassword( String email) throws EntityNotFoundException {
        getNext().forgotPassword(email);
    }

    @Override
    default AbstractUser resetPassword(ResetPasswordForm form) throws EntityNotFoundException, BadEntityException {
        return getNext().resetPassword(form);
    }

    @Override
    default void changePassword(AbstractUser user, ChangePasswordForm changePasswordForm) throws EntityNotFoundException, BadEntityException {
        getNext().changePassword(user,changePasswordForm);
    }

    @Override
    default void requestEmailChange(AbstractUser user, RequestEmailChangeForm emailChangeForm) throws EntityNotFoundException, AlreadyRegisteredException {
        getNext().requestEmailChange(user,emailChangeForm);
    }

    @Override
    default AbstractUser changeEmail(AbstractUser user,  /*@NotBlank*/ String changeEmailCode) throws EntityNotFoundException, BadEntityException {
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

//    @Override
//    default Map<String, String> fetchFullToken(String authHeader) {
//        return getNext().fetchFullToken(authHeader);
//    }

    @Override
    default AbstractUser createAdminUser(AuthProperties.Admin admin) throws BadEntityException, AlreadyRegisteredException {
        return getNext().createAdminUser(admin);
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
    default AbstractUser update(AbstractUser entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        return getNext().update(entity,full);
    }
    
}
