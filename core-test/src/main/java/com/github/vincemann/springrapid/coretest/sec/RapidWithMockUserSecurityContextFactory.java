package com.github.vincemann.springrapid.coretest.sec;

import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.google.common.collect.Sets;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
