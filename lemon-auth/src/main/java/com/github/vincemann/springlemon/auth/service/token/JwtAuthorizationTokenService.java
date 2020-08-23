package com.github.vincemann.springlemon.auth.service.token;

import com.github.vincemann.springlemon.auth.properties.LemonProperties;
import com.github.vincemann.springlemon.auth.security.JwtPrincipalConverter;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.nimbusds.jwt.JWTClaimsSet;

import java.util.Map;

public class JwtAuthorizationTokenService<P extends RapidAuthenticatedPrincipal> implements AuthorizationTokenService<P> {
    private JwsTokenService jwsTokenService;
    private JwtPrincipalConverter<P> jwtPrincipalConverter;
    private LemonProperties properties;


    @Override
    public String createToken(P principal) {
        Map<String, Object> claims = jwtPrincipalConverter.toClaims(principal);
//        Object subject = claims.remove("sub");
        return jwsTokenService.createToken(AUTH_AUDIENCE, principal.getName(), properties.getJwt().getExpirationMillis(), claims);
    }

    @Override
    public P parseToken(String token) {
        JWTClaimsSet jwtClaimsSet = jwsTokenService.parseToken(token, AUTH_AUDIENCE);
        return jwtPrincipalConverter.toPrincipal(jwtClaimsSet.getClaims());
    }
}
