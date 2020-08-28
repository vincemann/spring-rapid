package com.github.vincemann.springlemon.auth.util;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.exceptions.util.LexUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashMap;
import java.util.Map;

/**
 * Useful helper methods
 * 
 * @author Sanjay Patel
 */
@Slf4j
public class ValidationUtils {
	

//	public static final String AUTHORIZATION_REQUEST_COOKIE_NAME = "lemon_oauth2_authorization_request";
//	public static final String LEMON_REDIRECT_URI_COOKIE_PARAM_NAME = "lemon_redirect_uri";



//	public static ApplicationContext applicationContext;
//
//	public ValidationUtils(ApplicationContext applicationContext) {
//
//		ValidationUtils.applicationContext = applicationContext;
//		log.info("Created");
//	}


//	public ValidationUtils() {
//		log.info("Created");
//	}

	/**
	 * Throws AccessDeniedException is not authorized
	 * 
	 * @param authorized
	 * @param messageKey
	 */
	public static void ensureAuthority(boolean authorized, String messageKey) {
		
		if (!authorized)
			throw new AccessDeniedException(LexUtils.getMessage(messageKey));
	}


	/**
	 * Throws BadCredentialsException if not valid
	 * 
	 * @param valid
	 * @param messageKey
	 */
	public static void ensureCredentials(boolean valid, String messageKey) {
		
		if (!valid)
			throw new BadCredentialsException(LexUtils.getMessage(messageKey));
	}

	/**
	 * Throws BadCredentialsException if
	 * user's credentials were updated after the JWT was issued
	 */
	public static void ensureCredentialsUpToDate(JWTClaimsSet claims, AbstractUser<?> user) {

		long issueTime = claims.getIssueTime().getTime();

		ValidationUtils.ensureCredentials(issueTime >= user.getCredentialsUpdatedMillis(),
				"com.naturalprogrammer.spring.obsoleteToken");
	}
	


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
