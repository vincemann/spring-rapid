package com.github.vincemann.springrapid.authexceptions;

import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.service.UserAuthTokenService;

import com.github.vincemann.springrapid.auth.RapidSecurityContext;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Authentication success handler for sending the response
 * to the client after successful authentication.
 *
 * Adds token to response.
 * 
 * @author Sanjay Patel
 * @modifiedBy vincemann
 */
public class AuthenticationSuccessHandler
	extends SimpleUrlAuthenticationSuccessHandler {

	private final Log log = LogFactory.getLog(AuthenticationSuccessHandler.class);


	private UserAuthTokenService authTokenService;

	
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {

        // Instead of handle(request, response, authentication),
		// the statements below are introduced
    	response.setStatus(HttpServletResponse.SC_OK);
		String token;
		try {
			token = authTokenService.createNewAuthToken();
		} catch (EntityNotFoundException e) {
			throw new RuntimeException("No authenticated Principal found",e);
		}
		response.addHeader(HttpHeaders.AUTHORIZATION, token);

    	// as done in the base class
    	clearAuthenticationAttributes(request);
        
        log.debug("Authentication succeeded for user: " + RapidSecurityContext.getName());
    }


	@Autowired
	@Root
	public void setAuthTokenService(UserAuthTokenService authTokenService) {
		this.authTokenService = authTokenService;
	}
}
