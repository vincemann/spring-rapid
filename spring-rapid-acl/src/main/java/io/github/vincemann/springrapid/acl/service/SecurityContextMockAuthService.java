package io.github.vincemann.springrapid.acl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class SecurityContextMockAuthService implements MockAuthService {

    @Transactional
    public void runAuthenticatedAs(Authentication authentication, Runnable privilegedRunnable){
        Authentication old = SecurityContextHolder.getContext().getAuthentication();
        log.debug("saving old Security Context Authentication " + old);
        log.debug("setting temporary new Security Context Authentication: " + authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        privilegedRunnable.run();
        //restore
        log.debug("setting old Security Context Authentication: " + old);
        SecurityContextHolder.getContext().setAuthentication(old);
    }
}
