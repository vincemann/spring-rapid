package com.github.vincemann.springrapid.auth.service.token;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.security.JwtClaimsToPrincipalConverter;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.auth.util.LemonMapUtils;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static com.github.vincemann.springrapid.auth.util.RapidJwt.AUTH_AUDIENCE;
import static com.github.vincemann.springrapid.auth.util.RapidJwt.create;

@Slf4j
public abstract class AbstractJwtAuthorizationTokenService<P extends RapidAuthenticatedPrincipal>
        implements AuthorizationTokenService<P>, AopLoggable {

    private static final String PRINCIPAL_CLAIMS_KEY = "rapid-principal";


    private JwsTokenService jwsTokenService;
    private JwtClaimsToPrincipalConverter<P> jwtPrincipalConverter;
    private AuthProperties properties;


    @LogInteraction
    @Override
    public String createToken(P principal) {
        Map<String, Object> principalClaims = jwtPrincipalConverter.toClaims(principal);
        JWTClaimsSet claims = create(AUTH_AUDIENCE,
                principal.getName(),
                properties.getJwt().getExpirationMillis(),
                LemonMapUtils.mapOf(PRINCIPAL_CLAIMS_KEY, principalClaims)
        );

        return jwsTokenService.createToken(claims);
    }

    @LogInteraction
    @Override
    public P parseToken(String token) throws BadTokenException {
        JWTClaimsSet jwtClaimsSet = jwsTokenService.parseToken(token);
        Map<String, Object> principalClaims = (Map<String, Object>) jwtClaimsSet.getClaim(PRINCIPAL_CLAIMS_KEY);
        P principal = jwtPrincipalConverter.toPrincipal(principalClaims);
        verifyToken(jwtClaimsSet, principal);
        return principal;
    }

    public void verifyToken(JWTClaimsSet claims, P principal) {
        RapidJwt.validateNotExpired(claims);
    }

    @Autowired
    public void injectJwsTokenService(JwsTokenService jwsTokenService) {
        this.jwsTokenService = jwsTokenService;
    }

    @Autowired
    public void injectJwtPrincipalConverter(JwtClaimsToPrincipalConverter<P> jwtPrincipalConverter) {
        this.jwtPrincipalConverter = jwtPrincipalConverter;
    }

    @Autowired
    public void injectProperties(AuthProperties properties) {
        this.properties = properties;
    }
}
