package io.github.vincemann.springrapid.coretest.auth;

import java.util.Set;

/**
 * @see MockAuthenticationTemplate
 */
public interface RapidMockAuthenticationTemplate extends MockAuthenticationTemplate{
    public void mockAs(String email, String password, Set<String> authorities);
}
