package com.github.vincemann.springrapid.coretest;

import com.github.vincemann.springrapid.core.security.AbstractRapidSecurityContext;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import org.assertj.core.util.Sets;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Convenience class for creating test principals that can be logged in with {@link com.github.vincemann.springrapid.core.security.RapidSecurityContext} for a test.
 *
 */
public class TestPrincipal {

    public static RapidAuthenticatedPrincipal create(String name,String... roles){
        RapidAuthenticatedPrincipal principal = new RapidAuthenticatedPrincipal();
        principal.setName(name);
        principal.setPassword(AbstractRapidSecurityContext.TEMP_USER_PASSWORD);
        principal.setRoles(Sets.newHashSet(Arrays.asList(roles)));
        return principal;
    }

    public static RapidAuthenticatedPrincipal withName(String name){
        RapidAuthenticatedPrincipal principal = new RapidAuthenticatedPrincipal();
        principal.setName(name);
        principal.setPassword(AbstractRapidSecurityContext.TEMP_USER_PASSWORD);
        principal.setRoles(new HashSet<>());
        return principal;
    }

    public static RapidAuthenticatedPrincipal withRoles(String... roles){
        RapidAuthenticatedPrincipal principal = new RapidAuthenticatedPrincipal();
        principal.setName(AbstractRapidSecurityContext.TEMP_USER_NAME);
        principal.setPassword(AbstractRapidSecurityContext.TEMP_USER_PASSWORD);
        principal.setRoles(Sets.newHashSet(Arrays.asList(roles)));
        return principal;
    }
}
