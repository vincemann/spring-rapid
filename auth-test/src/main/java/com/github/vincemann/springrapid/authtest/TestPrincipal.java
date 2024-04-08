package com.github.vincemann.springrapid.authtest;

import com.github.vincemann.springrapid.auth.AuthPrincipal;
import com.github.vincemann.springrapid.auth.RapidSecurityContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Convenience class for creating test principals that can be logged in with {@link RapidSecurityContext} for a test.
 *
 */
public class TestPrincipal {

    public static AuthPrincipal create(String name, String... roles){
        AuthPrincipal principal = new AuthPrincipal();
        principal.setName(name);
        principal.setPassword("password");
        principal.setRoles(new HashSet<>(Arrays.asList(roles)));
        return principal;
    }


    public static AuthPrincipal withRoles(String... roles){
        AuthPrincipal principal = new AuthPrincipal();
        principal.setName("test user");
        principal.setPassword("password");
        principal.setRoles(new HashSet<>(Arrays.asList(roles)));
        return principal;
    }
}
