package com.github.vincemann.springrapid.auth.service.token;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.security.AuthenticatedPrincipalImpl;
import com.github.vincemann.springrapid.core.slicing.WebComponent;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Convert token to {@link AuthenticatedPrincipalImpl} and vice versa.
 */
@LogInteraction
@WebComponent
public interface AuthorizationTokenService<P extends AuthenticatedPrincipalImpl> {

    public String createToken(P principal);
    public P parseToken(String token) throws BadTokenException, BadCredentialsException;
}
