package com.github.vincemann.springrapid.acl.service;

import com.google.common.collect.Sets;
import com.github.vincemann.springrapid.acl.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class SecurityContextMockAuthService implements MockAuthService {

    @Transactional
    public void runAuthenticatedAs(Authentication authentication, Runnable runnable){
        Authentication old = SecurityContextHolder.getContext().getAuthentication();
        log.debug("saving old Security Context Authentication " + old);
        log.debug("setting temporary new Security Context Authentication: " + authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        runnable.run();
        //restore
        log.debug("setting old Security Context Authentication: " + old);
        SecurityContextHolder.getContext().setAuthentication(old);
    }

    @Override
    public void runAuthenticatedAsAdmin(Runnable privRunnable) {
        runAuthenticatedWith(Sets.newHashSet(Role.ADMIN),privRunnable);
    }
}
