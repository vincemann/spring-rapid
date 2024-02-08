package com.github.vincemann.springrapid.auth.service.token;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Convert token to {@link RapidPrincipal} and vice versa.
 */
@LogInteraction
public interface AuthorizationTokenService {

    public String createToken(RapidPrincipal principal);
    public RapidPrincipal parseToken(String token) throws BadTokenException, BadCredentialsException;
}
