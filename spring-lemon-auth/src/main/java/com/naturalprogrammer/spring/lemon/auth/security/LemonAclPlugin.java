package com.naturalprogrammer.spring.lemon.auth.security;

import com.naturalprogrammer.spring.lemon.auth.properties.LemonProperties;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUserRepository;
import com.naturalprogrammer.spring.lemon.auth.util.LemonUtils;
import io.github.vincemann.springrapid.acl.plugin.AbstractAclPlugin;
import io.github.vincemann.springrapid.acl.service.LocalPermissionService;
import io.github.vincemann.springrapid.acl.service.MockAuthService;
import io.github.vincemann.springrapid.core.proxy.CalledByProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;

import java.util.Optional;


@Slf4j
public class LemonAclPlugin extends AbstractAclPlugin {
    private AbstractUserRepository repository;

    public LemonAclPlugin(LocalPermissionService permissionService, MutableAclService mutableAclService, MockAuthService mockAuthService, AbstractUserRepository repository) {
        super(permissionService, mutableAclService, mockAuthService);
        this.repository = repository;
    }

    @CalledByProxy
    public void onAfterSave(AbstractUser toSave,AbstractUser saved){
        savePostSignupAclInfo(saved.getEmail());
    }

    @CalledByProxy
    public void onAfterSignup(AbstractUser registerAttempt,AbstractUser saved){
        savePostSignupAclInfo(registerAttempt.getEmail());
    }

    @CalledByProxy
    public void onAfterCreateAdminUser(LemonProperties.Admin admin){
        savePostSignupAclInfo(admin.getEmail());
    }

    private void savePostSignupAclInfo(String email){
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
