package com.github.vincemann.springlemon.auth.service.token;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;

/**
 * Convert token to {@link com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal} and vice versa.
 */
public interface AuthorizationTokenService<P extends RapidAuthenticatedPrincipal> {
    String AUTH_AUDIENCE = "auth";

    public String createToken(P principal);
    public P parseToken(String token);
}
