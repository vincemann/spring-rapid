package com.github.vincemann.springrapid.authtest;

import com.github.vincemann.springrapid.auth.AuthPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RapidWithMockUserSecurityContextFactory implements WithSecurityContextFactory<WithRapidMockUser> {

    public SecurityContext createSecurityContext(WithRapidMockUser withUser) {
        String username = StringUtils.hasLength(withUser.username()) ? withUser
                .username() : withUser.value();
        if (username == null) {
            throw new IllegalArgumentException(withUser
                    + " cannot have null username on both username and value properites");
        }

        AuthPrincipal principal = new AuthPrincipal(username, withUser.password(),
                new HashSet<>(Arrays.asList(withUser.roles())), withUser.id().isEmpty() ? null : withUser.id());
        return AuthTestUtil.createMockSecurityContext(principal);
    }
}
