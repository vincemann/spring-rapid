package com.naturalprogrammer.spring.lemon.auth.service;

import com.google.common.collect.Sets;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonRole;
import io.github.vincemann.springrapid.acl.service.SecurityContextMockAuthService;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

public class LemonMockAuthService extends SecurityContextMockAuthService {

    @Override
    public void runAuthenticatedAsAdmin(Runnable privRunnable) {
        runAuthenticatedWith(Sets.newHashSet(LemonRole.GOOD_ADMIN),privRunnable);
    }
}
