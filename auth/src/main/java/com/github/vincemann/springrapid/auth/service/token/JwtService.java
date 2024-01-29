package com.github.vincemann.springrapid.auth.service.token;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.stereotype.Component;

@Component
@LogInteraction(Severity.TRACE)
public interface JwtService extends AopLoggable {

	String TOKEN_PREFIX = "Bearer ";

	String createToken(JWTClaimsSet claimsSet);
	JWTClaimsSet parseToken(String token) throws BadTokenException;
}