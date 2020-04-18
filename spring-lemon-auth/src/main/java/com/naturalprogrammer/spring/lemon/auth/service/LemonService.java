package com.naturalprogrammer.spring.lemon.auth.service;

import com.naturalprogrammer.spring.lemon.auth.LemonProperties;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.ChangePasswordForm;
import com.naturalprogrammer.spring.lemon.auth.domain.ResetPasswordForm;
import io.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public interface LemonService<U extends AbstractUser<ID>, ID extends Serializable> extends CrudService<U,ID, JpaRepository<U,ID>> {
    public Map<String, Object> getContext(Optional<Long> expirationMillis, HttpServletResponse response);
    public void signup(U user);
    public void resendVerificationMail(U user);
    public U findByEmail(String email);
    public void verifyUser(ID userId, String verificationCode);
    public void forgotPassword(String email);
    public void resetPassword(ResetPasswordForm form);
    public U processUser(U user);
    public String changePassword(U user, ChangePasswordForm changePasswordForm);
    public void requestEmailChange(U user, U updatedUser);
    public void changeEmail(ID userId, String changeEmailCode);
    public String fetchNewToken(Optional<Long> expirationMillis,
                                Optional<String> optionalUsername);
    public Map<String, String> fetchFullToken(String authHeader);
    public void createAdminUser(LemonProperties.Admin admin);
    public abstract ID toId(String id);
    public void addAuthHeader(HttpServletResponse response, String username, Long expirationMillis);
}
