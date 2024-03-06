package com.github.vincemann.springrapid.auth.service.token;


import com.nimbusds.jwt.JWTClaimsSet;


public interface JwtService {

	String TOKEN_PREFIX = "Bearer ";

	String createToken(JWTClaimsSet claimsSet);
	JWTClaimsSet parseToken(String token) throws BadTokenException;
}