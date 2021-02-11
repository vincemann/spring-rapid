package com.github.vincemann.springrapid.coretest.login;

import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;

public interface MockLoginTemplate<P> {

    public void login(P principal);
}
