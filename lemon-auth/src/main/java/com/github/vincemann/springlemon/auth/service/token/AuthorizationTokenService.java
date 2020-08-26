package com.github.vincemann.springlemon.auth.service.token;

import com.github.vincemann.aoplog.api.LogException;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Convert token to {@link com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal} and vice versa.
 */
@LogInteraction
@LogException
public interface AuthorizationTokenService<P extends RapidAuthenticatedPrincipal> {

    public String createToken(P principal);
    public P parseToken(String token) throws BadTokenException, BadCredentialsException;
}
