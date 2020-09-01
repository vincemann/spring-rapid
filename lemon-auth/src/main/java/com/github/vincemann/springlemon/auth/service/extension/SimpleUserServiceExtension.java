package com.github.vincemann.springlemon.auth.service.extension;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springlemon.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springlemon.auth.service.SimpleUserService;
import com.github.vincemann.springlemon.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.core.proxy.SimpleCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public interface SimpleUserServiceExtension<S extends SimpleUserService>
        extends SimpleCrudServiceExtension<S>, SimpleUserService
{

    @Override
    default Map<String, Object> getSharedProperties(){
        return getNext().getSharedProperties();
    }

    @Override
    default AbstractUser signup(AbstractUser user) throws BadEntityException {
        return getNext().signup(user);
    }

    @Override
    default void resendVerificationMail(AbstractUser user) throws EntityNotFoundException {
        getNext().resendVerificationMail(user);
    }

    @Override
    default AbstractUser findByEmail(@Valid @Email @NotBlank String email) throws EntityNotFoundException {
        return getNext().findByEmail(email);
    }

    @Override
    default AbstractUser verifyUser(AbstractUser user, String verificationCode) throws EntityNotFoundException, BadTokenException {
        return getNext().verifyUser(user,verificationCode);
    }

    @Override
    default void forgotPassword(@Valid @Email @NotBlank String email) throws EntityNotFoundException {
        getNext().forgotPassword(email);
    }

    @Override
    default AbstractUser resetPassword(@Valid ResetPasswordForm form) throws EntityNotFoundException, BadTokenException {
        return getNext().resetPassword(form);
    }

    @Override
    default void changePassword(AbstractUser user, @Valid ChangePasswordForm changePasswordForm) throws EntityNotFoundException {
        getNext().changePassword(user,changePasswordForm);
    }

    @Override
    default void requestEmailChange(AbstractUser user, @Valid RequestEmailChangeForm emailChangeForm) throws EntityNotFoundException {
        getNext().requestEmailChange(user,emailChangeForm);
    }

    @Override
    default AbstractUser changeEmail(AbstractUser user, @Valid @NotBlank String changeEmailCode) throws EntityNotFoundException, BadTokenException {
        return getNext().changeEmail(user,changeEmailCode);
    }

    @Override
    default String fetchNewAuthToken(Optional optionalUsername) {
        return getNext().fetchNewAuthToken(optionalUsername);
    }

//    @Override
//    default Map<String, String> fetchFullToken(String authHeader) {
//        return getNext().fetchFullToken(authHeader);
//    }

    @Override
    default void createAdminUser(LemonProperties.Admin admin) throws BadEntityException {
        getNext().createAdminUser(admin);
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
