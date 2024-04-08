package com.github.vincemann.springrapid.auth.jwt;

import com.github.vincemann.springrapid.auth.AuthPrincipal;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Map;


public interface JwtPrincipalConverter {

    /**
     * Creates {@link AuthPrincipal} from Jwt claims.
     * @throws AuthenticationCredentialsNotFoundException  if expected claims not given ( = not present in token)
     */
    AuthPrincipal toPrincipal(Map<String,Object> claims) throws AuthenticationCredentialsNotFoundException;

    Map<String,Object> toClaims(AuthPrincipal user);
}
