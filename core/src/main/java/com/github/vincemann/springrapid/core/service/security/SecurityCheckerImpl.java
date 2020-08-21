package com.github.vincemann.springrapid.core.service.security;

import com.github.vincemann.springrapid.core.util.Authenticated;
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
    public boolean isAuthenticated() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context==null){
            return false;
        }
        Authentication authentication = context.getAuthentication();
        if (authentication==null){
            return false;
        }
        return true;
    }

    @Override
    public void checkRole(String role) throws AccessDeniedException {
        checkAuthenticated();
        if (!hasRole(role)){
            throw new AccessDeniedException("User does not have requested role: " + role);
        }
    }

    @Override
    public boolean hasRole(String role) {
        return Authenticated.getRoles().contains(role);
    }
}
