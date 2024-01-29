package com.github.vincemann.springrapid.auth.sec.service;

import com.github.vincemann.springrapid.auth.service.token.*;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

@Slf4j
public class RapidJweServiceTests {
	

	// An aes-128-cbc key generated at https://asecuritysite.com/encryption/keygen (take the "key" field)
	private static final String SECRET1 = "926D96C90030DD58429D2751AC1BDBBC";
	private static final String SECRET2 = "538518AB685B514685DA8055C03DDA63";
		 
	private JweServiceImpl jweService1;
	private JweServiceImpl jweService2;
//	private LemonJwsService jwsService1;
//	private LemonJwsService jwsService2;

	public RapidJweServiceTests() throws JOSEException {
		
		jweService1 = new JweServiceImpl(SECRET1);

		jweService2 = new JweServiceImpl(SECRET2);

//		jwsService1 = new LemonJwsService(SECRET1);
//		jwsService2 = new LemonJwsService(SECRET2);
	}
	
	@Test
	public void testParseToken() throws BadTokenException {
		testParseToken(jweService1);
		testParseToken(jweService2);
	}
	
	private void testParseToken(JweTokenService service) throws BadTokenException {
		
		log.info("Creating token ..." + service.getClass().getSimpleName());
		JWTClaimsSet claims = RapidJwt.create("auth", "subject", 5000L,
				MapUtils.mapOf("username", "abc@example.com"));
		String token = service.createToken(claims);
		
		log.info("Parsing token ...");
		JWTClaimsSet parsedClaims = service.parseToken(token);
		RapidJwt.validate(parsedClaims,"auth");
		
		log.info("Parsed token.");
		Assertions.assertEquals("subject", parsedClaims.getSubject());
		Assertions.assertEquals("abc@example.com", parsedClaims.getClaim("username"));
	}

	@Test
	public void testParseJweTokenWrongAudience() {
		Assertions.assertThrows(AccessDeniedException.class,() -> testParseTokenWrongAudience(jweService1));
	}
	
//	@Test
//	public void testParseJwsTokenWrongAudience() {
//
//		Assertions.assertThrows(BadCredentialsException.class,() -> testParseTokenWrongAudience(jwsService1));
//	}
	

	private void testParseTokenWrongAudience(JweTokenService service) throws BadTokenException {
		JWTClaimsSet claims = RapidJwt.create("auth", "subject", 5000L);
		String token = service.createToken(claims);
		JWTClaimsSet parsedClaims = service.parseToken(token);
		RapidJwt.validate( parsedClaims,"auth2");
	}

	@Test
	public void testParseJweTokenExpired() throws InterruptedException {

		Assertions.assertThrows(AccessDeniedException.class,() -> testParseTokenExpired(jweService1));
	}
	
//	@Test
//	public void testParseJwsTokenExpired() throws InterruptedException {
//
//		Assertions.assertThrows(BadCredentialsException.class,() -> testParseTokenExpired(jwsService1));
//	}

	private void testParseTokenExpired(JweTokenService service) throws InterruptedException, BadTokenException {
		
		String token = service.createToken(RapidJwt.create("auth", "subject", 1L));
		Thread.sleep(1L);
		JWTClaimsSet claims = service.parseToken(token);
		RapidJwt.validate(claims,"auth");
	}

	@Test()
	public void testParseJweTokenWrongSecret() {

		Assertions.assertThrows(BadTokenException.class,() -> testParseTokenWrongSecret(jweService1, jweService2));
	}

//	@Test()
//	public void testParseJwsTokenWrongSecret() {
//
//		Assertions.assertThrows(BadCredentialsException.class,() -> testParseTokenWrongSecret(jwsService1, jwsService2));
//	}

	private void testParseTokenWrongSecret(JweTokenService service1, JweTokenService service2) throws BadTokenException {
		
		String token = service1.createToken(RapidJwt.create("auth", "subject", 5000L));
		JWTClaimsSet claims = service2.parseToken(token);
		RapidJwt.validate(claims, "auth");
	}

	@Test()
	public void testParseJweTokenCutoffTime() throws InterruptedException {

		Assertions.assertThrows(AccessDeniedException.class,() -> testParseTokenCutoffTime(jweService1));
	}

//	@Test()
//	public void testParseJwsTokenCutoffTime() throws InterruptedException {
//
//		Assertions.assertThrows(BadCredentialsException.class,() -> testParseTokenCutoffTime(jwsService1));
//	}


	private void testParseTokenCutoffTime(JweTokenService service) throws InterruptedException, BadTokenException {
		
		String token = service.createToken(RapidJwt.create("auth", "subject", 5000L));
		Thread.sleep(1L);
		JWTClaimsSet claims = service.parseToken(token);
		RapidJwt.validate(claims,"auth", System.currentTimeMillis());
	}
}
