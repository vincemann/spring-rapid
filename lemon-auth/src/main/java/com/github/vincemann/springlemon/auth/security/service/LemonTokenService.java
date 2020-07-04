package com.github.vincemann.springlemon.auth.security.service;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.nimbusds.jwt.JWTClaimsSet;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

import java.util.Map;

@ServiceComponent
@LogInteraction(Severity.TRACE)
public interface LemonTokenService extends AopLoggable {

	String LEMON_IAT = "lemon-iat";

	String createToken(String aud, String subject, Long expirationMillis, Map<String, Object> claimMap);
	String createToken(String audience, String subject, Long expirationMillis);
	//Audience = Whos the recipient of the token
	JWTClaimsSet parseToken(String token, String audience);
	JWTClaimsSet parseToken(String token, String audience, long issuedAfter);
	<T> T parseClaim(String token, String claim);
}