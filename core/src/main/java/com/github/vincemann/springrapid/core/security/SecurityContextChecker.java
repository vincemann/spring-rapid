package com.github.vincemann.springrapid.core.security;

import com.github.vincemann.springrapid.core.util.VerifyAccess;
import org.springframework.security.access.AccessDeniedException;

/**
 * Convenience wrapper for accessing data from {@link RapidSecurityContext}.
 */
public class SecurityContextChecker {

    private SecurityContextChecker(){}

    public static void checkAuthenticated() throws AccessDeniedException {
        boolean authenticated = RapidSecurityContext.isAuthenticated();
        VerifyAccess.condition(authenticated,"No Authenticated User");
    }


    public static void checkHasRoles(String... roles) throws AccessDeniedException {
        checkAuthenticated();
        for (String required : roles) {
            if (!RapidSecurityContext.hasRole(required)){
                throw new AccessDeniedException("User does not have requested role: " + required);
            }
        }
    }

    public static void checkDoesNotHaveRoles(String... roles) throws AccessDeniedException {
        checkAuthenticated();
        for (String required : roles) {
            if (RapidSecurityContext.hasRole(required)){
                throw new AccessDeniedException("User has forbidden role: " + required);
            }
        }
    }

}
