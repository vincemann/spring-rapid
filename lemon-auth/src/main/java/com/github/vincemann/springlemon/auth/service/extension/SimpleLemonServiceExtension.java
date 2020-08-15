package com.github.vincemann.springlemon.auth.service.extension;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springlemon.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springlemon.auth.properties.LemonProperties;
import com.github.vincemann.springlemon.auth.service.SimpleLemonService;
import com.github.vincemann.springrapid.core.proxy.SimpleCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public interface SimpleLemonServiceExtension<S extends SimpleLemonService>
        extends SimpleCrudServiceExtension<S>, SimpleLemonService
{

    @Override
    default Map<String, Object> getContext(Optional expirationMillis, HttpServletResponse response) {
        return getNext().getContext(expirationMillis,response);
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
    default AbstractUser verifyUser(AbstractUser user, String verificationCode) throws EntityNotFoundException {
        return getNext().verifyUser(user,verificationCode);
    }

    @Override
    default void forgotPassword(@Valid @Email @NotBlank String email) throws EntityNotFoundException {
        getNext().forgotPassword(email);
    }

    @Override
    default AbstractUser resetPassword(@Valid ResetPasswordForm form) throws EntityNotFoundException {
        return getNext().resetPassword(form);
    }

    @Override
    default String changePassword(AbstractUser user, @Valid ChangePasswordForm changePasswordForm) throws EntityNotFoundException {
        return getNext().changePassword(user,changePasswordForm);
    }

    @Override
    default void requestEmailChange(AbstractUser user, @Valid RequestEmailChangeForm emailChangeForm) throws EntityNotFoundException {
        getNext().requestEmailChange(user,emailChangeForm);
    }

    @Override
    default AbstractUser changeEmail(AbstractUser user, @Valid @NotBlank String changeEmailCode) throws EntityNotFoundException {
        return getNext().changeEmail(user,changeEmailCode);
    }

    @Override
    default String fetchNewToken(Optional expirationMillis, Optional optionalUsername) {
        return getNext().fetchNewToken(expirationMillis,optionalUsername);
    }

    @Override
    default Map<String, String> fetchFullToken(String authHeader) {
        return getNext().fetchFullToken(authHeader);
    }

    @Override
    default void createAdminUser(LemonProperties.Admin admin) throws BadEntityException {
        getNext().createAdminUser(admin);
    }

    @Override
    default Serializable toId(String id) {
        return getNext().toId(id);
    }

    @Override
    default void addAuthHeader(HttpServletResponse response, String username, Long expirationMillis) {
        getNext().addAuthHeader(response,username,expirationMillis);
    }

    @Override
    default AbstractUser update(AbstractUser entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        return getNext().update(entity,full);
    }
    
}
