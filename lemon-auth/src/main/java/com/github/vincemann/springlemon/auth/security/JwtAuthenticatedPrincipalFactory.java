package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springrapid.core.service.security.AbstractAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.util.Authenticated;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

public interface JwtAuthenticatedPrincipalFactory {
    /**
     * Creates Principal gettable via {@link Authenticated#get()} from Jwt claims extracted from jwt token.
     * @throws AuthenticationCredentialsNotFoundException  if expected claims not given ( = not present in token)
     */
    public AbstractAuthenticatedPrincipal create(JWTClaimsSet claims) throws AuthenticationCredentialsNotFoundException;
}
