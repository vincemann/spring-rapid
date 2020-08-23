package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Map;
import java.util.Set;

public interface JwtPrincipalConverter<P extends RapidAuthenticatedPrincipal> {

    /**
     * Creates {@link AbstractUser} from Jwt claims.
     * @throws AuthenticationCredentialsNotFoundException  if expected claims not given ( = not present in token)
     */
    public P toPrincipal(Map<String,Object> claims) throws AuthenticationCredentialsNotFoundException;

    public Map<String,Object> toClaims(P user);
}
