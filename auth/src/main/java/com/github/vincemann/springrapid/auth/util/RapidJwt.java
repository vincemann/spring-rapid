package com.github.vincemann.springrapid.auth.util;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper-Methods for creating and validating Jwt's claims
 */
@Slf4j
public class RapidJwt {

	public static final String AUTH_AUDIENCE = "auth";
	// do not change these to the standard exp and iat bc the jwt builder will convert to date obj again and round to seconds, which fucks up the tests
	public static final String EXPIRATION_AUDIENCE = "expired";
	public static final String ISSUED_AT_AUDIENCE = "issued-at";



	public static JWTClaimsSet create(String aud, String subject, long expirationMillis, Map<String,Object> otherClaims){
		JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

		builder
				//.issueTime(new Date())
//                .expirationTime(new Date()) -> rounds to millis bad for tests
				.claim(RapidJwt.EXPIRATION_AUDIENCE,System.currentTimeMillis() + expirationMillis)
				.audience(aud)
				.subject(subject)
				.claim(RapidJwt.ISSUED_AT_AUDIENCE,System.currentTimeMillis());
//                .issueTime(new Date()); -> rounds to millis bad for tests

		otherClaims.forEach(builder::claim);
		return builder.build();
	}

	public static JWTClaimsSet create(String aud, String subject, long expirationMillis){
		return create(aud,subject,expirationMillis,new HashMap<>());
	}

	public static void validateNotExpired(JWTClaimsSet claims) {
		long expirationTime = (long) claims.getClaim(RapidJwt.EXPIRATION_AUDIENCE);
		long currentTime = System.currentTimeMillis();
		log.debug("Check if toke is expired...");
		log.debug("Expiration time = " + new Date(expirationTime)
				+ ". Current time = " + new Date(currentTime));
		log.debug("Expiration time = " + expirationTime
				+ ". Current time = " + currentTime);
		if (expirationTime< currentTime){
			throw new AccessDeniedException("Expired token");
		}
		log.debug("Token not expired.");
	}

	public static void validate(JWTClaimsSet claims, String expectedAud,long issuedAfter) {
		validateNotExpired(claims);
		validateAud(claims,expectedAud);
		validateIssuedAfter(claims,issuedAfter);
	}

	public static void validate(JWTClaimsSet claims, String expectedAud) {
		validateNotExpired(claims);
		validateAud(claims,expectedAud);
	}

	public static void validateAud(JWTClaimsSet claims, String expectedAud) {
		if (expectedAud==null || !claims.getAudience().contains(expectedAud)){
			throw new AccessDeniedException("Wrong token audience");
		}
	}

	public static void validateIssuedAfter(JWTClaimsSet claims, long issuedAfter)  {
		log.debug("Check if token is obsolete...");
		long issueTime = (long) claims.getClaim(ISSUED_AT_AUDIENCE);
		log.debug("Token issued at: " + new Date(issueTime) +  ", must be issued after: " + new Date(issuedAfter));
		log.debug("Token issued at: " + issueTime +  ", must be issued after: " + issuedAfter);
		if (issueTime < issuedAfter){
			throw new AccessDeniedException("Token has become obsolete");
		}
		log.debug("Token is not obsolete.");
	}
	

//	public static final String AUTHORIZATION_REQUEST_COOKIE_NAME = "lemon_oauth2_authorization_request";
//	public static final String LEMON_REDIRECT_URI_COOKIE_PARAM_NAME = "lemon_redirect_uri";



//	public static ApplicationContext applicationContext;
//
//	public ValidationUtils(ApplicationContext applicationContext) {
//
//		ValidationUtils.applicationContext = applicationContext;
//
//	}


//	public ValidationUtils() {
//
//	}

//	/**
//	 * Throws AccessDeniedException is not authorized
//	 *
//	 * @param authorized
//	 * @param messageKey
//	 */
//	public static void ensureAuthority(boolean authorized, String messageKey) {
//
//		if (!authorized)
//			throw new AccessDeniedException(Message.get(messageKey));
//	}




	


//	/**
//	 * Gets the reference to an application-context bean
//	 *
//	 * @param clazz	the type of the bean
//	 */
//	public static <T> T getBean(Class<T> clazz) {
//		return applicationContext.getBean(clazz);
//	}


//	/**
//	 * Generates a random unique string
//	 */
//	public static String uid() {
//
//		return UUID.randomUUID().toString();
//	}





}
