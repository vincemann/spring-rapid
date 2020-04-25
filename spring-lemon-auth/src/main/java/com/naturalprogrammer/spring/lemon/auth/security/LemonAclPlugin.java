package com.naturalprogrammer.spring.lemon.auth.security;

import com.naturalprogrammer.spring.lemon.auth.LemonProperties;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUserRepository;
import io.github.vincemann.springrapid.acl.plugin.AbstractAclPlugin;
import io.github.vincemann.springrapid.acl.service.LocalPermissionService;
import io.github.vincemann.springrapid.core.proxy.CalledByProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;

import java.util.Optional;


@Slf4j
public class LemonAclPlugin extends AbstractAclPlugin {
    private AbstractUserRepository repository;

    public LemonAclPlugin(LocalPermissionService permissionService, MutableAclService mutableAclService, AbstractUserRepository repository) {
        super(permissionService, mutableAclService);
        this.repository = repository;
    }

    @CalledByProxy
    public void onAfterSignup(AbstractUser registerAttempt){
        savePostSignupAclInfo(registerAttempt.getEmail());
    }

    @CalledByProxy
    public void onAfterCreateAdmin(LemonProperties.Admin admin){
        savePostSignupAclInfo(admin.getUsername());
    }

    private void savePostSignupAclInfo(String email){
        Optional<AbstractUser> byEmail = repository.findByEmail(email);
        if(!byEmail.isPresent()){
            log.warn("No user found after signup -> cant save acl permissions");
            return;
        }
        savePermissionForAuthenticatedOver(byEmail.get(), BasePermission.ADMINISTRATION);
        saveFullPermissionForAdminOver(byEmail.get());
    }



}
