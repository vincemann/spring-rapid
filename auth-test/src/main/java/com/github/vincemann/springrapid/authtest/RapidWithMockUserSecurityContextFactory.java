package com.github.vincemann.springrapid.authtest;

import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import com.google.common.collect.Sets;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.StringUtils;

public class RapidWithMockUserSecurityContextFactory implements WithSecurityContextFactory<WithRapidMockUser> {

    public SecurityContext createSecurityContext(WithRapidMockUser withUser) {
        String username = StringUtils.hasLength(withUser.username()) ? withUser
                .username() : withUser.value();
        if (username == null) {
            throw new IllegalArgumentException(withUser
                    + " cannot have null username on both username and value properites");
        }

        RapidPrincipal principal = new RapidPrincipal(username, withUser.password(),
                Sets.newHashSet(withUser.authorities()), withUser.id().isEmpty() ? null : withUser.id());
        return RapidTestUtil.createMockSecurityContext(principal);
    }
}
