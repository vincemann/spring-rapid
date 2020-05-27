package io.github.spring.lemon.auth.security.service;

import io.github.spring.lemon.auth.util.LecUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

public class LemonJwtServiceTests {
	
	private static final Log log = LogFactory.getLog(LemonJwtServiceTests.class);	

	// An aes-128-cbc key generated at https://asecuritysite.com/encryption/keygen (take the "key" field)
	private static final String SECRET1 = "926D96C90030DD58429D2751AC1BDBBC";
	private static final String SECRET2 = "538518AB685B514685DA8055C03DDA63";
		 
	private LemonJweService jweService1;
	private LemonJweService jweService2;
	private LemonJwsService jwsService1;
	private LemonJwsService jwsService2;

	public LemonJwtServiceTests() throws JOSEException {
		
		jweService1 = new LemonJweService(SECRET1);
		jweService2 = new LemonJweService(SECRET2);

		jwsService1 = new LemonJwsService(SECRET1);
		jwsService2 = new LemonJwsService(SECRET2);
	}
	
	@Test
	public void testParseToken() {
		
		testParseToken(jweService1);
		testParseToken(jwsService1);
	}
	
	private void testParseToken(LemonTokenService service) {
		
		log.info("Creating token ..." + service.getClass().getSimpleName());
		String token = service.createToken("auth", "subject", 5000L,
				LecUtils.mapOf("username", "abc@example.com"));
		
		log.info("Parsing token ...");
		JWTClaimsSet claims = service.parseToken(token, "auth");
		
		log.info("Parsed token.");
		Assertions.assertEquals("subject", claims.getSubject());
		Assertions.assertEquals("abc@example.com", claims.getClaim("username"));
	}

	@Test
	public void testParseJweTokenWrongAudience() {
		Assertions.assertThrows(BadCredentialsException.class,() -> testParseTokenWrongAudience(jweService1));
	}
	
	@Test
	public void testParseJwsTokenWrongAudience() {

		Assertions.assertThrows(BadCredentialsException.class,() -> testParseTokenWrongAudience(jwsService1));
	}
	

	private void testParseTokenWrongAudience(LemonTokenService service) {
		
		String token = service.createToken("auth", "subject", 5000L);
		service.parseToken(token, "auth2");
	}

	@Test
	public void testParseJweTokenExpired() throws InterruptedException {

		Assertions.assertThrows(BadCredentialsException.class,() -> testParseTokenExpired(jweService1));
	}
	
	@Test
	public void testParseJwsTokenExpired() throws InterruptedException {

		Assertions.assertThrows(BadCredentialsException.class,() -> testParseTokenExpired(jwsService1));
	}

	private void testParseTokenExpired(LemonTokenService service) throws InterruptedException {
		
		String token = service.createToken("auth", "subject", 1L);
		Thread.sleep(1L);
		service.parseToken(token, "auth");
	}

	@Test()
	public void testParseJweTokenWrongSecret() {

		Assertions.assertThrows(BadCredentialsException.class,() -> testParseTokenWrongSecret(jweService1, jweService2));
	}

	@Test()
	public void testParseJwsTokenWrongSecret() {

		Assertions.assertThrows(BadCredentialsException.class,() -> testParseTokenWrongSecret(jwsService1, jwsService2));
	}

	private void testParseTokenWrongSecret(LemonTokenService service1, LemonTokenService service2) {
		
		String token = service1.createToken("auth", "subject", 5000L);
		service2.parseToken(token, "auth");
	}

	@Test()
	public void testParseJweTokenCutoffTime() throws InterruptedException {

		Assertions.assertThrows(BadCredentialsException.class,() -> testParseTokenCutoffTime(jweService1));
	}

	@Test()
	public void testParseJwsTokenCutoffTime() throws InterruptedException {

		Assertions.assertThrows(BadCredentialsException.class,() -> testParseTokenCutoffTime(jwsService1));
	}


	private void testParseTokenCutoffTime(LemonTokenService service) throws InterruptedException {
		
		String token = service.createToken("auth", "subject", 5000L);
		Thread.sleep(1L);				
		service.parseToken(token, "auth", System.currentTimeMillis());
	}
}
