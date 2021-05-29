package com.github.vincemann.springrapid.auth.security;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Map;


public interface JwtClaimsToPrincipalConverter<P extends RapidAuthenticatedPrincipal> {

    /**
     * Creates {@link AbstractUser} from Jwt claims.
     * @throws AuthenticationCredentialsNotFoundException  if expected claims not given ( = not present in token)
     */
    public P toPrincipal(Map<String,Object> claims) throws AuthenticationCredentialsNotFoundException;

    public Map<String,Object> toClaims(P user);
}
