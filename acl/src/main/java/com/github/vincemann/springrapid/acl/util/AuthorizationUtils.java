package com.github.vincemann.springrapid.acl.util;

import com.github.vincemann.springrapid.auth.RapidSecurityContext;
import com.github.vincemann.springrapid.auth.util.VerifyAccess;
import org.springframework.security.access.AccessDeniedException;

public class AuthorizationUtils {

    private AuthorizationUtils(){}

    public static void assertAuthenticated() throws AccessDeniedException {
        VerifyAccess.isTrue(RapidSecurityContext.isAuthenticated(),"Must be authenticated");
    }


    public static void assertHasRoles(String... roles) throws AccessDeniedException {
        assertAuthenticated();
        for (String role : roles) {
            VerifyAccess.isTrue(
                    RapidSecurityContext.hasRole(role),"User does not have required role: " + role);
        }
    }

    public static void assertNotHasRoles(String... roles) throws AccessDeniedException {
        assertAuthenticated();
        for (String role : roles) {
            VerifyAccess.isTrue(
                    !RapidSecurityContext.hasRole(role),"User has forbidden role: " + role);
        }
    }

}
