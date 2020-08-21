package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.Map;

public interface JwtClaimsUserConverter {

    /**
     * Creates {@link AbstractUser} from Jwt claims.
     * @throws AuthenticationCredentialsNotFoundException  if expected claims not given ( = not present in token)
     */
    public AbstractUser toUser(JWTClaimsSet claims) throws AuthenticationCredentialsNotFoundException;

    public Map<String,Object> toClaimsPayload(AbstractUser user);
}
