package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.service.AuthorizationTokenService;
import com.github.vincemann.springrapid.core.service.security.AbstractAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.util.MapperUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

public class LemonJwtAuthenticatedPrincipalFactory implements JwtAuthenticatedPrincipalFactory {
    @Override
    public AbstractAuthenticatedPrincipal create(JWTClaimsSet claims) throws AuthenticationCredentialsNotFoundException {
        Object userClaim = claims.getClaim(AuthorizationTokenService.USER_CLAIM);

        if (userClaim == null)
            throw new AuthenticationCredentialsNotFoundException("User claim with key: " + AuthorizationTokenService.USER_CLAIM + " not found");

        return MapperUtils.deserialize((String) userClaim);
    }
}
