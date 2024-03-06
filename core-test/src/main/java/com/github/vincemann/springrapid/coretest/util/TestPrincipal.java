package com.github.vincemann.springrapid.coretest.util;

import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import org.assertj.core.util.Sets;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Convenience class for creating test principals that can be logged in with {@link RapidSecurityContext} for a test.
 *
 */
public class TestPrincipal {

    public static RapidPrincipal create(String name, String... roles){
        RapidPrincipal principal = new RapidPrincipal();
        principal.setName(name);
        principal.setPassword("password");
        principal.setRoles(Sets.newHashSet(Arrays.asList(roles)));
        return principal;
    }

    public static RapidPrincipal withName(String name){
        RapidPrincipal principal = new RapidPrincipal();
        principal.setName(name);
        principal.setPassword("password");
        principal.setRoles(new HashSet<>());
        return principal;
    }

    public static RapidPrincipal withRoles(String... roles){
        RapidPrincipal principal = new RapidPrincipal();
        principal.setName("test user");
        principal.setPassword("password");
        principal.setRoles(Sets.newHashSet(Arrays.asList(roles)));
        return principal;
    }
}
