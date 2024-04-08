package com.github.vincemann.springrapid.auth.jwt;


import com.nimbusds.jwt.JWTClaimsSet;


public interface JwtService {

	String TOKEN_PREFIX = "Bearer ";

	String createToken(JWTClaimsSet claimsSet);
	JWTClaimsSet parseToken(String token) throws BadTokenException;
}