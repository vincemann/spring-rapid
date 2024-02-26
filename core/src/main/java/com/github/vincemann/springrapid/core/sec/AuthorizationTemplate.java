package com.github.vincemann.springrapid.core.sec;

import com.github.vincemann.springrapid.core.util.VerifyAccess;
import org.springframework.security.access.AccessDeniedException;

public class AuthorizationTemplate {

    private AuthorizationTemplate(){}

    public static void assertAuthenticated() throws AccessDeniedException {
        boolean authenticated = RapidSecurityContext.isAuthenticated();
        VerifyAccess.condition(authenticated,"No Authenticated User");
    }


    public static void assertHasRoles(String... roles) throws AccessDeniedException {
        assertAuthenticated();
        for (String required : roles) {
            if (!RapidSecurityContext.hasRole(required)){
                throw new AccessDeniedException("User does not have requested role: " + required);
            }
        }
    }

    public static void assertNotHasRoles(String... roles) throws AccessDeniedException {
        assertAuthenticated();
        for (String required : roles) {
            if (RapidSecurityContext.hasRole(required)){
                throw new AccessDeniedException("User has forbidden role: " + required);
            }
        }
    }

}
