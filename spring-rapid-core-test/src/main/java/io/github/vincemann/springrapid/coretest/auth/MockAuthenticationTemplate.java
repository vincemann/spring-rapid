package io.github.vincemann.springrapid.coretest.auth;


import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;

/**
 * Mocks Springs {@link SecurityContext} and {@link Authentication}, and lets user dynamically
 * set its own mocked SecurityContext/ Authentication.
 *
 * Use this class when {@link org.springframework.security.test.context.support.WithMockUser} and similar Spring Mocking support
 * is not enough. (Usually when mocking dynamically is required)
 *
 * See extending interfaces for more useful methods.
 */
public interface MockAuthenticationTemplate {
    public void enableMocking();
    public void disableMocking();
    public boolean isMocked();
    public void setUpAuthMocks();


}
