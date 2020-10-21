package com.github.vincemann.springrapid.auth.service.token;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.BadCredentialsException;

import java.text.ParseException;

/**
 * JWS Service
 * Token only gets signed, not encrypted.
 * Encryption should be performed via TLS/HTTPS -> double encryption is avoided.
 *
 *
 * 
 * Reference: https://connect2id.com/products/nimbus-jose-jwt/examples/jws-with-hmac
 */
public class RapidJwsService extends JsonJwtService implements JwsTokenService {

	private JWSSigner signer;
	private JWSVerifier verifier;

	public RapidJwsService(String secret) throws JOSEException {
		
		byte[] secretKey = secret.getBytes();
		signer = new MACSigner(secret);
		verifier = new MACVerifier(secret);
	}

	@Override
	public String createToken(JWTClaimsSet jwtClaimsSet) {
		
		Payload payload = createPayload(jwtClaimsSet);

	   	// Prepare JWS object
		JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), payload);

		try {
			// Apply the HMAC
			jwsObject.sign(signer);
			
		} catch (JOSEException e) {
			
			throw new RuntimeException(e);
		}

		// To serialize to compact form, produces something like
		// eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
		return jwsObject.serialize();
	}

	/**
	 * Parses a token
	 */
	@Override
	public JWTClaimsSet parseToken(String token) {
		
		// Parse the JWS and verify it, e.g. on client-side
		JWSObject jwsObject;

		try {
			jwsObject = JWSObject.parse(token);
			if (jwsObject.verify(verifier))
				return JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
			
		} catch (JOSEException | ParseException e) {
			
			throw new BadCredentialsException(e.getMessage());
		}

		throw new BadCredentialsException("JWS verification failed!");
	}
}
