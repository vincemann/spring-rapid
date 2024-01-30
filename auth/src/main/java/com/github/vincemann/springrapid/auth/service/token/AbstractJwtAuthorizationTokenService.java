package com.github.vincemann.springrapid.auth.service.token;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.sec.JwtPrincipalConverter;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static com.github.vincemann.springrapid.auth.util.RapidJwt.AUTH_CLAIM;
import static com.github.vincemann.springrapid.auth.util.RapidJwt.create;

@Slf4j
public abstract class AbstractJwtAuthorizationTokenService
        implements AuthorizationTokenService, AopLoggable {

    private static final String PRINCIPAL_CLAIMS_KEY = "rapid-principal";


    private JwsTokenService jwsTokenService;
    private JwtPrincipalConverter jwtPrincipalConverter;
    private AuthProperties properties;


    @LogInteraction
    @Override
    public String createToken(RapidPrincipal principal) {
        Map<String, Object> principalClaims = jwtPrincipalConverter.toClaims(principal);
        JWTClaimsSet claims = create(AUTH_CLAIM,
                principal.getName(),
                properties.getJwt().getExpirationMillis(),
                MapUtils.mapOf(PRINCIPAL_CLAIMS_KEY, principalClaims)
        );

        return jwsTokenService.createToken(claims);
    }

    @LogInteraction
    @Override
    public RapidPrincipal parseToken(String token) throws BadTokenException {
        JWTClaimsSet jwtClaimsSet = jwsTokenService.parseToken(token);
        Map<String, Object> principalClaims = (Map<String, Object>) jwtClaimsSet.getClaim(PRINCIPAL_CLAIMS_KEY);
        RapidPrincipal principal = jwtPrincipalConverter.toPrincipal(principalClaims);
        verifyToken(jwtClaimsSet, principal);
        return principal;
    }

    public void verifyToken(JWTClaimsSet claims, RapidPrincipal principal) {
        RapidJwt.validateNotExpired(claims);
    }

    @Autowired
    public void setJwsTokenService(JwsTokenService jwsTokenService) {
        this.jwsTokenService = jwsTokenService;
    }

    @Autowired
    public void setJwtPrincipalConverter(JwtPrincipalConverter jwtPrincipalConverter) {
        this.jwtPrincipalConverter = jwtPrincipalConverter;
    }

    @Autowired
    public void setProperties(AuthProperties properties) {
        this.properties = properties;
    }
}
