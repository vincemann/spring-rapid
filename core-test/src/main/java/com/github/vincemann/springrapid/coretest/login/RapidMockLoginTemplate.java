package com.github.vincemann.springrapid.coretest.login;

import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @see com.github.vincemann.springrapid.coretest.TestPrincipal
 */
public class RapidMockLoginTemplate implements MockLoginTemplate<RapidAuthenticatedPrincipal> {
    private RapidSecurityContext<RapidAuthenticatedPrincipal> rapidSecurityContext;

    @Override
    public void login(RapidAuthenticatedPrincipal principal) {
        rapidSecurityContext.login(principal);
    }

    @Autowired
    public void injectRapidSecurityContext(RapidSecurityContext<RapidAuthenticatedPrincipal> rapidSecurityContext) {
        this.rapidSecurityContext = rapidSecurityContext;
    }
}
