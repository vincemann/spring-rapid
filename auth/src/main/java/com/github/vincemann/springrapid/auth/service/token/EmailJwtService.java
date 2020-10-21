package com.github.vincemann.springrapid.auth.service.token;

import com.nimbusds.jwt.JWTClaimsSet;

import java.util.Map;

/**
 * Creates Jwt tokens, that are ready to be send via email and parses them.
 * Also offers convenience methods for verifying and setting typical jwt claims.
 */
public interface EmailJwtService {


    String createToken(String aud, String subject, long expirationMillis);
    String createToken(String aud, String subject, long expirationMillis, Map<String,Object> otherClaims);

    JWTClaimsSet parseToken(String token, String expectedAud) throws BadTokenException;
    JWTClaimsSet parseToken(String token, String expectedAud,long issuedAfter) throws BadTokenException;
}
