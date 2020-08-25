package com.github.vincemann.springlemon.auth.service.token;

import com.github.vincemann.springlemon.auth.properties.LemonProperties;
import com.github.vincemann.springlemon.auth.security.JwtClaimsPrincipalConverter;
import com.github.vincemann.springlemon.auth.util.LecUtils;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtAuthorizationTokenService<P extends RapidAuthenticatedPrincipal> implements AuthorizationTokenService<P> {

    private static final String AUTH_AUDIENCE = "auth";
    private static final String PRINCIPAL_CLAIMS_KEY = "rapid-principal";

    /**
     * Time when this JWT was created
     */
//    private static final String ISSUED_AT_CLAIM_KEY = "rapid-iat";


    private JwsTokenService jwsTokenService;
    private JwtClaimsPrincipalConverter<P> jwtPrincipalConverter;
    private LemonProperties properties;


    @Autowired
    public JwtAuthorizationTokenService(JwsTokenService jwsTokenService, JwtClaimsPrincipalConverter<P> jwtPrincipalConverter, LemonProperties properties) {
        this.jwsTokenService = jwsTokenService;
        this.jwtPrincipalConverter = jwtPrincipalConverter;
        this.properties = properties;
    }

    @Override
    public String createToken(P principal) {
        Map<String, Object> principalClaims = jwtPrincipalConverter.toClaims(principal);
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

        JWTClaimsSet claims = builder
                //.issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + properties.getJwt().getExpirationMillis()))
                .audience(AUTH_AUDIENCE)
                .subject(principal.getName())
                .issueTime(new Date())
                .claim(PRINCIPAL_CLAIMS_KEY,principalClaims)
                .build();
//                .claim(ISSUED_AT_CLAIM_KEY, System.currentTimeMillis());



//        Object subject = claims.remove("sub");
        return jwsTokenService.createToken(claims);
    }

    @Override
    public P parseToken(String token) {
        JWTClaimsSet jwtClaimsSet = jwsTokenService.parseToken(token);
        verifyToken(jwtClaimsSet);
        Map<String, Object> principalClaims = (Map<String, Object>) jwtClaimsSet.getClaim(PRINCIPAL_CLAIMS_KEY);
        return jwtPrincipalConverter.toPrincipal(principalClaims);
    }

    protected void verifyToken(JWTClaimsSet claims){
        //expired?
        long expirationTime = claims.getExpirationTime().getTime();
        long currentTime = System.currentTimeMillis();

        log.debug("Parsing JWT. Expiration time = " + expirationTime
                + ". Current time = " + currentTime);

        LecUtils.ensureCredentials(expirationTime >= currentTime,
                "com.naturalprogrammer.spring.expiredToken");

        //todo put into EmailVerificationTokenService
//        //not yet valid?
//        //is token still valid?
//        long issueTime = claims.getIssueTime().getTime();
//        LecUtils.ensureCredentials(issueTime >= issuedAfter,
//                "com.naturalprogrammer.spring.obsoleteToken");

    }
}
