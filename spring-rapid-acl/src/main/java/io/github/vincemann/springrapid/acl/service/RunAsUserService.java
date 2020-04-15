package io.github.vincemann.springrapid.acl.service;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.springframework.security.core.Authentication;

@ServiceComponent
public interface RunAsUserService {
    public void runAuthenticatedAs(Authentication authentication, Runnable privilegedRunnable);
}
