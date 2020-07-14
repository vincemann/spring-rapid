package com.github.vincemann.springlemon.auth.service;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogException;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springlemon.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springlemon.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springlemon.auth.properties.LemonProperties;
import com.github.vincemann.springlemon.auth.util.UserUtils;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Validated
@ServiceComponent
@LogException
public interface LemonService<U extends AbstractUser<ID>, ID extends Serializable, R extends AbstractUserRepository<U,ID>>
        extends CrudService<U,ID, R>, AopLoggable {
    public Map<String, Object> getContext(Optional<Long> expirationMillis, HttpServletResponse response);
    @Validated(UserUtils.SignUpValidation.class)
    public U signup(@Valid U user) throws BadEntityException;
    public void resendVerificationMail(U user);
    public U findByEmail(@Valid @Email @NotBlank String email);
    public U verifyUser(ID userId, String verificationCode);
    public void forgotPassword(@Valid @Email @NotBlank String email);
    public U resetPassword(@Valid ResetPasswordForm form);
    public String changePassword(U user, @Valid ChangePasswordForm changePasswordForm);
    @Validated(UserUtils.ChangeEmailValidation.class)
    public void requestEmailChange(ID userId, @Valid RequestEmailChangeForm emailChangeForm);
    public U changeEmail(ID userId, @Valid @NotBlank String changeEmailCode);
    public String fetchNewToken(Optional<Long> expirationMillis, Optional<String> optionalUsername);
    public Map<String, String> fetchFullToken(String authHeader);
    public void createAdminUser(LemonProperties.Admin admin) throws BadEntityException;
    @LogInteraction(Severity.TRACE)
    public abstract ID toId(String id);
    @LogInteraction(Severity.TRACE)
    public void addAuthHeader(HttpServletResponse response, String username, Long expirationMillis);
    @Validated(UserUtils.UpdateValidation.class)
    @Override
    U update(U entity, Boolean full) throws EntityNotFoundException,  BadEntityException;
//    public U updateUser(U user, @Valid U updatedUser) throws BadEntityException, EntityNotFoundException;
}
