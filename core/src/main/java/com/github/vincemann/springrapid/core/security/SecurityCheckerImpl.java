package com.github.vincemann.springrapid.core.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityCheckerImpl implements SecurityChecker {

    @Override
    public void checkAuthenticated() throws AccessDeniedException {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context==null){
            throw new AccessDeniedException("Security Context is null");
        }
        Authentication authentication = context.getAuthentication();
        if (authentication==null){
            throw new AccessDeniedException("Authentication null");
        }
    }


    @Override
    public void checkRole(String role) throws AccessDeniedException {
        checkAuthenticated();
        if (!RapidSecurityContext.hasRole(role)){
            throw new AccessDeniedException("User does not have requested role: " + role);
        }
    }


}
