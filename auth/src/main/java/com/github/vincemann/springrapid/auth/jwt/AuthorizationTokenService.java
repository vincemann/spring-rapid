package com.github.vincemann.springrapid.auth.jwt;

import com.github.vincemann.springrapid.auth.AuthPrincipal;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Convert token to {@link AuthPrincipal} and vice versa.
 */
public interface AuthorizationTokenService {

    public String createToken(AuthPrincipal principal);
    public AuthPrincipal parseToken(String token) throws BadTokenException, BadCredentialsException;
}
