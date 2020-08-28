package com.github.vincemann.springlemon.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import com.github.vincemann.springlemon.auth.service.LemonService;
import com.github.vincemann.springlemon.auth.util.LecwUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Authentication success handler for sending the response
 * to the client after successful authentication.
 * 
 * @author Sanjay Patel
 */
@Slf4j
public class LemonAuthenticationSuccessHandler
	extends SimpleUrlAuthenticationSuccessHandler {
	

    private ObjectMapper objectMapper;    
    private LemonService<?, ?,?> lemonService;
	private LemonProperties properties;

	public LemonAuthenticationSuccessHandler(ObjectMapper objectMapper, LemonService<?, ?,?> lemonService, LemonProperties properties) {
		
		this.objectMapper = objectMapper;
		this.lemonService = lemonService;
		this.properties = properties;
		log.info("Created");
	}

	
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {

        // Instead of handle(request, response, authentication),
		// the statements below are introduced
    	response.setStatus(HttpServletResponse.SC_OK);
    	response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    	lemonService.fetchNewAuthToken(Optional.empty());
    	
    	// write current-user data to the response  
    	response.getOutputStream().print(
    			objectMapper.writeValueAsString(currentUser));

    	// as done in the base class
    	clearAuthenticationAttributes(request);
        
        log.debug("Authentication succeeded for user: " + currentUser);        
    }
}
