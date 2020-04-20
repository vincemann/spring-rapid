package com.naturalprogrammer.spring.lemon.auth.service;

import com.naturalprogrammer.spring.lemon.auth.LemonProperties;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.ChangePasswordForm;
import com.naturalprogrammer.spring.lemon.auth.domain.ResetPasswordForm;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonUserDto;
import com.naturalprogrammer.spring.lemon.auth.util.UserUtils;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Validated
public interface LemonService<U extends AbstractUser<ID>, ID extends Serializable> extends CrudService<U,ID, JpaRepository<U,ID>> {
    public Map<String, Object> getContext(Optional<Long> expirationMillis, HttpServletResponse response);
    @Validated(UserUtils.SignUpValidation.class)
    public void signup(@Valid U user);
    public void resendVerificationMail(U user);
    public U findByEmail(@Valid @Email @NotBlank String email);
    public void verifyUser(ID userId, String verificationCode);
    public void forgotPassword(@Valid @Email @NotBlank String email);
    public void resetPassword(@Valid ResetPasswordForm form);
    public String changePassword(U user, @Valid ChangePasswordForm changePasswordForm);
    @Validated(UserUtils.ChangeEmailValidation.class)
    public void requestEmailChange(ID userId, @Valid U updatedUser);
    public void changeEmail(ID userId, @Valid @NotBlank String changeEmailCode);
    public String fetchNewToken(Optional<Long> expirationMillis, Optional<String> optionalUsername);
    public Map<String, String> fetchFullToken(String authHeader);
    public void createAdminUser(LemonProperties.Admin admin);
    public abstract ID toId(String id);
    public void addAuthHeader(HttpServletResponse response, String username, Long expirationMillis);
    @Validated(UserUtils.UpdateValidation.class)
    public LemonUserDto updateUser(U user, @Valid U updatedUser) throws BadEntityException, EntityNotFoundException;
}
