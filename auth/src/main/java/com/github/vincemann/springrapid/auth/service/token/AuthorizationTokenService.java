package com.github.vincemann.springrapid.auth.service.token;

import com.github.vincemann.aoplog.api.LogException;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.slicing.WebComponent;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Convert token to {@link com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal} and vice versa.
 */
@LogInteraction
//@LogException
@WebComponent
public interface AuthorizationTokenService<P extends RapidAuthenticatedPrincipal> {

    public String createToken(P principal);
    public P parseToken(String token) throws BadTokenException, BadCredentialsException;
}
