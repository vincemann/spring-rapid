package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springlemon.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springlemon.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springlemon.auth.properties.LemonProperties;
import com.github.vincemann.springlemon.auth.service.LemonService;
import com.github.vincemann.springlemon.auth.util.LemonUtils;
import com.github.vincemann.springrapid.acl.plugin.AbstractAclServiceExtension;
import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Transactional
public class LemonAclServiceExtension
        extends AbstractAclServiceExtension<LemonService>
            implements LemonService, CrudServiceExtension<LemonService>
{
    //todo rename methods and switch from proxies naming convention to annotations + add @LogInteraction when method names say something
    private AbstractUserRepository repository;

    public LemonAclServiceExtension(LocalPermissionService permissionService, MutableAclService mutableAclService, MockAuthService mockAuthService, AbstractUserRepository repository) {
        super(permissionService, mutableAclService, mockAuthService);
        this.repository = repository;
    }



    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        AbstractUser saved = (AbstractUser) getNext().save(entity);
        savePostSignupAclInfo(saved.getEmail());
        return saved;
    }


    @Override
    public AbstractUser update(AbstractUser entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        return getNext().update(entity,full);
    }



    @Override
    public Map<String, Object> getContext(Optional expirationMillis, HttpServletResponse response) {
        return getNext().getContext(expirationMillis,response);
    }

    @Override
    public AbstractUser signup(@Valid AbstractUser user) throws BadEntityException {
        AbstractUser saved = getNext().signup(user);
        savePostSignupAclInfo(user.getEmail());
        return saved;
    }

    @Override
    public void resendVerificationMail(AbstractUser user) {
        getNext().resendVerificationMail(user);
    }

    @Override
    public AbstractUser findByEmail(@Valid @Email @NotBlank String email) {
        return getNext().findByEmail(email);
    }

    @Override
    public AbstractUser verifyUser(Serializable userId, String verificationCode) {
        return getNext().verifyUser(userId,verificationCode);
    }

    @Override
    public void forgotPassword(@Valid @Email @NotBlank String email) {
        getNext().forgotPassword(email);
    }

    @Override
    public AbstractUser resetPassword(@Valid ResetPasswordForm form) {
        return getNext().resetPassword(form);
    }

    @Override
    public String changePassword(AbstractUser user, @Valid ChangePasswordForm changePasswordForm) {
        return getNext().changePassword(user,changePasswordForm);
    }

    @Override
    public void requestEmailChange(Serializable userId, @Valid RequestEmailChangeForm emailChangeForm) {
        getNext().requestEmailChange(userId,emailChangeForm);
    }

    @Override
    public AbstractUser changeEmail(Serializable userId, @Valid @NotBlank String changeEmailCode) {
        return getNext().changeEmail(userId,changeEmailCode);
    }


    @Override
    public String fetchNewToken(Optional expirationMillis, Optional optionalUsername) {
        return getNext().fetchNewToken(expirationMillis,optionalUsername);
    }

    @Override
    public Map<String, String> fetchFullToken(String authHeader) {
        return getNext().fetchFullToken(authHeader);
    }

    @Override
    public void createAdminUser(LemonProperties.Admin admin) throws BadEntityException {
        getNext().createAdminUser(admin);
        savePostSignupAclInfo(admin.getEmail());
    }

    @Override
    public Serializable toId(String id) {
        return getNext().toId(id);
    }

    @Override
    public void addAuthHeader(HttpServletResponse response, String username, Long expirationMillis) {
        getNext().addAuthHeader(response,username,expirationMillis);
    }


    public void savePostSignupAclInfo(String email){
        log.debug("saving acl info for signed up user: " + email);
        Optional<AbstractUser> byEmail = repository.findByEmail(email);
        if(!byEmail.isPresent()){
            log.warn("No user found after signup -> cant save acl permissions");
            return;
        }
        //login is needed for save full permission for authenticated
        LemonUtils.login(byEmail.get());
        savePermissionForAuthenticatedOver(byEmail.get(), BasePermission.ADMINISTRATION);
        saveFullPermissionForAdminOver(byEmail.get());
    }



}
