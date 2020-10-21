package com.github.vincemann.springrapid.auth.util;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.core.util.Message;
import com.github.vincemann.springrapid.core.util.VerifyAccess;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;

/**
 * Useful helper methods
 * 
 * @author Sanjay Patel
 */
@Slf4j
public class JwtUtils {

	/**
	 * Throws BadCredentialsException if
	 * user's credentials were updated after the JWT was issued
	 */
	public static void ensureCredentialsUpToDate(JWTClaimsSet claims, AbstractUser<?> user) {

		long issueTime = claims.getIssueTime().getTime();

		VerifyAccess.condition(issueTime >= user.getCredentialsUpdatedMillis(),
				Message.get("com.naturalprogrammer.spring.obsoleteToken"));
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
