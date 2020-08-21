package com.github.vincemann.springlemon.auth.service;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.nimbusds.jwt.JWTClaimsSet;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

import java.util.Map;

@ServiceComponent
@LogInteraction(Severity.TRACE)
public interface JwtService extends AopLoggable {

	/**
	 * Time when this JWT was created
	 */
	String LEMON_IAT = "lemon-iat";

	/**
	 * Create JWT with specific payload.
	 * The first key-value pairs of the payload are mandatory,
	 * the rest of the payload is obligatory and can contain anything.
	 *
	 * @param aud				What is this token for?
	 * @param subject			Who is this token for?
	 * @param expirationMillis	When will this token expire?
	 * @param claimMap			payload
	 * @return					token
	 */
	String createToken(String aud, String subject, Long expirationMillis, Map<String, Object> claimMap);
	String createToken(String audience, String subject, Long expirationMillis);

	//Audience = Whos the recipient of the token
	JWTClaimsSet parseToken(String token, String audience);
	JWTClaimsSet parseToken(String token, String audience, long issuedAfter);

	<T> T parseClaim(String token, String claim);
}