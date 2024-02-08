package com.github.vincemann.springrapid.auth.handler;

import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.token.HttpTokenService;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RapidAuthenticationSuccessHandler
	extends SimpleUrlAuthenticationSuccessHandler {
	

    private UserService userService;
    private HttpTokenService httpTokenService;
	private CoreProperties properties;
	
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {

        // Instead of handle(request, response, authentication),
		// the statements below are introduced
    	response.setStatus(HttpServletResponse.SC_OK);
    	response.setContentType(properties.getController().getMediaType());
		String token = null;
		try {
			token = userService.createNewAuthToken();
		} catch (EntityNotFoundException e) {
			throw new RuntimeException("No authenticated Principal found",e);
		}
		response.addHeader(HttpHeaders.AUTHORIZATION, token);

//    	// write current-user data to the response
//    	response.getOutputStream().print(
//    			objectMapper.writeValueAsString(currentUser));

    	// as done in the base class
    	clearAuthenticationAttributes(request);
        
        log.debug("Authentication succeeded for user: " + RapidSecurityContext.getName());
    }

    @Autowired
	public void setProperties(CoreProperties properties) {
		this.properties = properties;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Autowired
	public void setHttpTokenService(HttpTokenService httpTokenService) {
		this.httpTokenService = httpTokenService;
	}
}
