package com.github.vincemann.springrapid.auth.service.token;

import com.github.vincemann.springrapid.auth.util.JwtUtils;
import com.github.vincemann.springrapid.core.util.Message;
import com.github.vincemann.springrapid.core.util.VerifyAccess;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EncryptedEmailJwtService implements EmailJwtService {


    private JweTokenService jweTokenService;

    public String createToken(String aud, String subject, long expirationMillis){
        return createToken(aud,subject,expirationMillis,new HashMap<>());
    }

    public String createToken(String aud, String subject, long expirationMillis, Map<String,Object> otherClaims)	{
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

        builder
                //.issueTime(new Date())
//                .expirationTime(new Date()) -> rounds to millis bad for tests
                .claim(JwtUtils.EXPIRATION_AUDIENCE,System.currentTimeMillis() + expirationMillis)
                .audience(aud)
                .subject(subject)
                .claim(JwtUtils.ISSUED_AT_AUDIENCE,System.currentTimeMillis());
//                .issueTime(new Date()); -> rounds to millis bad for tests

        otherClaims.forEach(builder::claim);
        JWTClaimsSet claims = builder.build();
        return jweTokenService.createToken(claims);
    }

    public JWTClaimsSet parseToken(String token, String expectedAud) throws BadTokenException {
        JWTClaimsSet claims = jweTokenService.parseToken(token);
        VerifyAccess.condition(expectedAud != null &&
                        claims.getAudience().contains(expectedAud),
                "com.naturalprogrammer.spring.wrong.audience");
        long expirationTime = (long) claims.getClaim(JwtUtils.EXPIRATION_AUDIENCE);
        long currentTime = System.currentTimeMillis();

        log.debug("Parsing JWT. Expiration time = " + expirationTime
                + ". Current time = " + currentTime);

        VerifyAccess.condition(expirationTime >= currentTime,
                Message.get("com.naturalprogrammer.spring.expiredToken"));
        return claims;
    }

    public JWTClaimsSet parseToken(String token, String expectedAud,long issuedAfter) throws BadTokenException {
        JWTClaimsSet claims = parseToken(token, expectedAud);
        long issueTime = (long) claims.getClaim(JwtUtils.ISSUED_AT_AUDIENCE);

//        log.debug("token issued at: " + new Date(issueTime) + ", user creds updated at: " + new Date(issuedAfter));
        VerifyAccess.condition(issueTime >= issuedAfter,
                Message.get("com.naturalprogrammer.spring.obsoleteToken"));
        return claims;
    }

    @Autowired
    public void injectJweTokenService(JweTokenService jweTokenService) {
        this.jweTokenService = jweTokenService;
    }
}
