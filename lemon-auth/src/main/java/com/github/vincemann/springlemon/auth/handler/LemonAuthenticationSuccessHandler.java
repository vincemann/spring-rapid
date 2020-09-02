package com.github.vincemann.springlemon.auth.handler;

import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springrapid.core.controller.RapidMediaType;
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
 * @author Sanjay Patel
 * @modifiedBy vincemann
 */
@Slf4j
public class LemonAuthenticationSuccessHandler
	extends SimpleUrlAuthenticationSuccessHandler {
	

    private UserService<?, ?,?> userService;
	private String mediaType;

	public LemonAuthenticationSuccessHandler(UserService<?, ?,?> userService) {
		this.userService = userService;
		log.info("Created");
	}

	
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {

        // Instead of handle(request, response, authentication),
		// the statements below are introduced
    	response.setStatus(HttpServletResponse.SC_OK);
    	response.setContentType(mediaType);
    	userService.fetchNewAuthToken();
    	
//    	// write current-user data to the response
//    	response.getOutputStream().print(
//    			objectMapper.writeValueAsString(currentUser));

    	// as done in the base class
    	clearAuthenticationAttributes(request);
        
        log.debug("Authentication succeeded for user: " + RapidSecurityContext.getName());
    }

    @RapidMediaType
	@Autowired
	public void injectMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
}
