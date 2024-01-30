package com.github.vincemann.springrapid.auth.sec;

import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Map;


public interface JwtPrincipalConverter {

    /**
     * Creates {@link RapidPrincipal} from Jwt claims.
     * @throws AuthenticationCredentialsNotFoundException  if expected claims not given ( = not present in token)
     */
    public RapidPrincipal toPrincipal(Map<String,Object> claims) throws AuthenticationCredentialsNotFoundException;

    public Map<String,Object> toClaims(RapidPrincipal user);
}
