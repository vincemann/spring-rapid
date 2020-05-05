package com.naturalprogrammer.spring.lemon.auth.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonPrincipal;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonUserDto;
import com.naturalprogrammer.spring.lemon.auth.security.service.BlueTokenService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.naturalprogrammer.spring.lemon.exceptions.util.LexUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Useful helper methods
 * 
 * @author Sanjay Patel
 */
public class LecUtils {
	
	private static final Log log = LogFactory.getLog(LecUtils.class);
	
	public static final String AUTHORIZATION_REQUEST_COOKIE_NAME = "lemon_oauth2_authorization_request";
	public static final String LEMON_REDIRECT_URI_COOKIE_PARAM_NAME = "lemon_redirect_uri";

	
	// JWT Token related
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final int TOKEN_PREFIX_LENGTH = 7;
	public static final String TOKEN_RESPONSE_HEADER_NAME = "Lemon-Authorization";


	public static ApplicationContext applicationContext;
	
	public LecUtils(ApplicationContext applicationContext) {
		
		LecUtils.applicationContext = applicationContext;
		log.info("Created");
	}
	
	
	/**
	 * Extracts the current-user from authentication object

	 * @return
	 */
	public static <ID extends Serializable> LemonUserDto currentUser(SecurityContext context) {
		return currentUser(context.getAuthentication());
	}

	
	/**
	 * Extracts the current-user from authentication object
	 * 
	 * @param auth
	 * @return
	 */
	public static <ID extends Serializable> LemonUserDto currentUser(Authentication auth) {
		
	    if (auth != null) {
	      Object principal = auth.getPrincipal();
	      if (principal instanceof LemonPrincipal) {
	        return ((LemonPrincipal) principal).currentUser();
	      }
	    }
	    return null;	  
	}


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
	 * Constructs a map of the key-value pairs,
	 * passed as parameters
	 * 
	 * @param keyValPair
	 */
	@SuppressWarnings("unchecked")
	public static <K,V> Map<K,V> mapOf(Object... keyValPair) {
		
	    if(keyValPair.length % 2 != 0)
	        throw new IllegalArgumentException("Keys and values must be in pairs");
	
	    Map<K,V> map = new HashMap<K,V>(keyValPair.length / 2);
	
	    for(int i = 0; i < keyValPair.length; i += 2){
	        map.put((K) keyValPair[i], (V) keyValPair[i+1]);
	    }
	
	    return map;
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
	 * Gets the reference to an application-context bean
	 *  
	 * @param clazz	the type of the bean
	 */
	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}


	/**
	 * Generates a random unique string
	 */
	public static String uid() {
		
		return UUID.randomUUID().toString();
	}





}
