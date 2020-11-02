package com.github.vincemann.springrapid.auth.handler;

import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.token.HttpTokenService;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
	

    private UserService<?, ?> UserService;
    private HttpTokenService httpTokenService;
	private CoreProperties properties;
	
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {

        // Instead of handle(request, response, authentication),
		// the statements below are introduced
    	response.setStatus(HttpServletResponse.SC_OK);
    	response.setContentType(properties.controller.mediaType);
		String token = UserService.createNewAuthToken();
		httpTokenService.appendToken(token,response);

//    	// write current-user data to the response
//    	response.getOutputStream().print(
//    			objectMapper.writeValueAsString(currentUser));

    	// as done in the base class
    	clearAuthenticationAttributes(request);
        
        log.debug("Authentication succeeded for user: " + RapidSecurityContext.getName());
    }

    @Autowired
	public void injectProperties(CoreProperties properties) {
		this.properties = properties;
	}

	@Autowired

	public void injectUserService(UserService<?, ?> userService) {
		this.UserService = userService;
	}
	
	@Autowired
	public void injectHttpTokenService(HttpTokenService httpTokenService) {
		this.httpTokenService = httpTokenService;
	}
}
