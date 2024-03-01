package com.github.vincemann.springrapid.core.sec;

import com.github.vincemann.springrapid.core.util.VerifyAccess;
import org.springframework.security.access.AccessDeniedException;

public class AuthorizationTemplate {

    private AuthorizationTemplate(){}

    public static void assertAuthenticated() throws AccessDeniedException {
        VerifyAccess.condition(RapidSecurityContext.isAuthenticated(),"Must be authenticated");
    }


    public static void assertHasRoles(String... roles) throws AccessDeniedException {
        assertAuthenticated();
        for (String role : roles) {
            VerifyAccess.condition(
                    RapidSecurityContext.hasRole(role),"User does not have required role: " + role);
        }
    }

    public static void assertNotHasRoles(String... roles) throws AccessDeniedException {
        assertAuthenticated();
        for (String role : roles) {
            VerifyAccess.condition(
                    !RapidSecurityContext.hasRole(role),"User has forbidden role: " + role);
        }
    }

}
