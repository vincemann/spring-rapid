package com.github.vincemann.springrapid.core.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class RapidSecurityChecker implements SecurityChecker {

    @Override
    public void checkAuthenticated() throws AccessDeniedException {
        boolean authenticated = RapidSecurityContext.isAuthenticated();
        if (!authenticated){
            throw new AccessDeniedException("No Authenticated User");
        }
    }


    @Override
    public void checkHasRoles(String... roles) throws AccessDeniedException {
        checkAuthenticated();
        for (String required : roles) {
            if (!RapidSecurityContext.hasRole(required)){
                throw new AccessDeniedException("User does not have requested role: " + required);
            }
        }
    }

    @Override
    public void checkHasNotRoles(String... roles) throws AccessDeniedException {
        checkAuthenticated();
        for (String required : roles) {
            if (RapidSecurityContext.hasRole(required)){
                throw new AccessDeniedException("User has forbidden role: " + required);
            }
        }
    }

}
