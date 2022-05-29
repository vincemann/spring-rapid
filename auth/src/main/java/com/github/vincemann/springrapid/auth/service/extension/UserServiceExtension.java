package com.github.vincemann.springrapid.auth.service.extension;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.RequestMediumChangeDto;
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
    default void resendVerificationMail(AbstractUser user) throws EntityNotFoundException, BadEntityException {
        getNext().resendVerificationMail(user);
    }

    @Override
    default Optional<AbstractUser> findByEmail( String email)  {
        return getNext().findByEmail(email);
    }

    @Override
    default AbstractUser verifyUser(String verificationCode) throws EntityNotFoundException,  BadEntityException {
        return getNext().verifyUser(verificationCode);
    }

    @Override
    default void forgotPassword( String email) throws EntityNotFoundException {
        getNext().forgotPassword(email);
    }

    @Override
    default AbstractUser resetPassword(ResetPasswordDto dto, String code) throws EntityNotFoundException, BadEntityException {
        return getNext().resetPassword(dto, code);
    }

    @Override
    default void changePassword(AbstractUser user, ChangePasswordDto changePasswordForm) throws EntityNotFoundException, BadEntityException {
        getNext().changePassword(user,changePasswordForm);
    }

    @Override
    default void requestEmailChange(AbstractUser user, RequestMediumChangeDto emailChangeForm) throws EntityNotFoundException, AlreadyRegisteredException {
        getNext().requestEmailChange(user,emailChangeForm);
    }

    @Override
    default AbstractUser changeEmail(/*@NotBlank*/ String changeEmailCode) throws EntityNotFoundException, BadEntityException {
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
