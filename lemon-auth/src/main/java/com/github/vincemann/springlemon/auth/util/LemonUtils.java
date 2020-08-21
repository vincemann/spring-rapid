package com.github.vincemann.springlemon.auth.util;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import com.github.vincemann.springlemon.auth.service.AuthorizationTokenService;
import com.github.vincemann.springlemon.auth.service.JwtService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.github.vincemann.springrapid.core.util.MapperUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;

/**
 * Useful helper methods
 * 
 * @author Sanjay Patel
 */
public class LemonUtils {
	
	private static final Log log = LogFactory.getLog(LemonUtils.class);

	public LemonUtils() {
		
		log.info("Created");
	}


//	/**
//	 * Signs a user in
//	 *
//	 * @param user
//	 */
//	public static <U extends AbstractUser<ID>, ID extends Serializable>
//	void login(U user) {
//
//		LemonAuthenticatedPrincipal principal = new LemonAuthenticatedPrincipal(user.toUserDto());
//
//		Authentication authentication = // make the authentication object
//	    	new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
//
//	    SecurityContextHolder.getContext().setAuthentication(authentication); // put that in the security context
//	    principal.eraseCredentials();
//	}
	
	
	/**
	 * Throws BadCredentialsException if 
	 * user's credentials were updated after the JWT was issued
	 */
	public static <U extends AbstractUser<ID>, ID extends Serializable>
	void ensureCredentialsUpToDate(JWTClaimsSet claims, U user) {
		
		long issueTime = (long) claims.getClaim(JwtService.LEMON_IAT);

		LecUtils.ensureCredentials(issueTime >= user.getCredentialsUpdatedMillis(),
				"com.naturalprogrammer.spring.obsoleteToken");
	}
//
//	public static LemonUserDto getUserDto(JWTClaimsSet claims) {
//
//		Object userClaim = claims.getClaim(AuthorizationTokenService.USER_CLAIM);
//
//		if (userClaim == null)
//			return null;
//
//		return MapperUtils.deserialize((String) userClaim);
//	}
}
