package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.properties.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springlemon.auth.util.LemonUtils;
import com.github.vincemann.springrapid.acl.plugin.AbstractAclPlugin;
import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.core.advice.log.LogInteraction;
import com.github.vincemann.springrapid.core.proxy.CalledByProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;

import java.awt.font.GlyphVector;
import java.util.Optional;


@Slf4j
public class LemonAclPlugin extends AbstractAclPlugin {
    private AbstractUserRepository repository;

    public LemonAclPlugin(LocalPermissionService permissionService, MutableAclService mutableAclService, MockAuthService mockAuthService, AbstractUserRepository repository) {
        super(permissionService, mutableAclService, mockAuthService);
        this.repository = repository;
    }

    @LogInteraction(level = LogInteraction.Level.TRACE)
    @CalledByProxy
    public void onAfterSave(AbstractUser toSave, AbstractUser saved){
        savePostSignupAclInfo(saved.getEmail());
    }

    @LogInteraction(level = LogInteraction.Level.TRACE)
    @CalledByProxy
    public void onAfterSignup(AbstractUser registerAttempt,AbstractUser saved){
        savePostSignupAclInfo(registerAttempt.getEmail());
    }

    @LogInteraction(level = LogInteraction.Level.TRACE)
    @CalledByProxy
    public void onAfterCreateAdminUser(LemonProperties.Admin admin){
        savePostSignupAclInfo(admin.getEmail());
    }

    @LogInteraction(level = LogInteraction.Level.TRACE)
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
