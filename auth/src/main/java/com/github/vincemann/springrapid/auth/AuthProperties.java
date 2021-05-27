package com.github.vincemann.springrapid.auth;

import com.github.vincemann.springrapid.core.CoreProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Validated
@Slf4j
@Getter
@Setter
public class AuthProperties {


	private static CoreProperties coreProperties;

	public AuthProperties(CoreProperties coreProperties) {
		AuthProperties.coreProperties = coreProperties;
		this.controller = new Controller();
	}

	public Controller controller;


	@Getter
	@Setter
	public static class Controller {

		public String userBaseUrl = coreProperties.baseUrl+"/user";

		public String loginUrl = coreProperties.baseUrl+"/login";
		public String pingUrl = coreProperties.baseUrl+"/ping";
		public String contextUrl = coreProperties.baseUrl+"/context";

		public String signupUrl = userBaseUrl +"/signup";
		public String resetPasswordUrl = userBaseUrl +"/reset-password";
		public String resetPasswordViewUrl = userBaseUrl +"/reset-password-view";
		public String fetchByEmailUrl = userBaseUrl +"/fetch-by-email";
		public String changeEmailUrl = userBaseUrl +"/change-email";
		public String verifyUserUrl = userBaseUrl +"/verify";
		public String resendVerificationEmailUrl = userBaseUrl +"/resend-verification-email";
		public String forgotPasswordUrl = userBaseUrl +"/forgot-password";
		public String changePasswordUrl = userBaseUrl +"/change-password";
		public String requestEmailChangeUrl = userBaseUrl +"/request-email-change";
		public String newAuthTokenUrl = userBaseUrl +"/new-auth-token";

	}

	public CoreProperties getCoreProperties() {
		return coreProperties;
	}

	//	/**
//	 * The default URL to redirect to after
//	 * a user logs in using OAuth2/OpenIDConnect
//	 */
//    public String oauth2AuthenticationSuccessUrl = "http://localhost:9000/social-login-success?token=";


    /**
	 * Recaptcha related properties
	 */
	public Recaptcha recaptcha = new Recaptcha();
	
    /**
	 * CORS related properties
	 */
	public Cors cors = new Cors();

	public int maxLoginAttempts = 25;

	public boolean loginBruteforceProtection = true;


	/**
	 * These admins will be created on Application start, if not present already
	 */
	public List<Admin> admins = new ArrayList<>();
	
	
	/**
     * Any shared properties you want to pass to the 
     * client should begin with lemon.shared.
     */
	public Map<String, Object> shared;

	/**
	 * JWT token generation related properties
	 */
	public Jwt jwt;


	/**************************
	 * Static classes
	 *************************/

	/**
     * Recaptcha related properties
     */
	@Getter
	@Setter
	public static class Recaptcha {
    	
		/**
         * Google ReCaptcha Site Key
         */
    	public String sitekey;
    	        
        /**
         * Google ReCaptcha Secret Key
         */
    	public String secretkey;
	}
	
	
    /**
     * CORS configuration related properties
     */
    @Getter
	@Setter
	public static class Cors {
		
		/**
		 * Comma separated whitelisted URLs for CORS.
		 * Should contain the applicationURL at the minimum.
		 * Not providing this property would disable CORS configuration.
		 */
		public String[] allowedOrigins;
		
		/**
		 * Methods to be allowed, e.g. GET,POST,...
		 */
		public String[] allowedMethods = {"GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "OPTIONS", "PATCH"};
		
		/**
		 * Request headers to be allowed, e.g. content-type,accept,origin,x-requested-with,...
		 */
		public String[] allowedHeaders = {
				"Accept",
				"Accept-Encoding",
				"Accept-Language",
				"Cache-Control",
				"Connection",
				"Content-Length",
				"Content-Type",
				"Cookie",
				"Host",
				"Origin",
				"Pragma",
				"Referer",
				"User-Agent",
				"x-requested-with",
				HttpHeaders.AUTHORIZATION};
		
		/**
		 * Response headers that you want to expose to the client JavaScript programmer, e.g. Lemon-Authorization.
		 * I don't think we need to mention here the headers that we don't want to access through JavaScript.
		 * Still, by default, we have provided most of the common headers.
		 *  
		 * <br>
		 * See <a href="http://stackoverflow.com/questions/25673089/why-is-access-control-expose-headers-needed#answer-25673446">
		 * here</a> to know why this could be needed.
		 */		
		public String[] exposedHeaders = {
				"Cache-Control",
				"Connection",
				"Content-Type",
				"Date",
				"Expires",
				"Pragma",
				"Server",
				"Set-Cookie",
				"Transfer-Encoding",
				"X-Content-Type-Options",
				"X-XSS-Protection",
				"X-Frame-Options",
				"X-Application-Context",
				HttpHeaders.AUTHORIZATION};
		
		/**
		 * CORS <code>maxAge</code> long property
		 */
		public long maxAge = 3600L;

	}

	
	/**
	 * Properties regarding the initial Admin user to be created
	 * 
	 * @author Sanjay Patel
	 */
	@AllArgsConstructor @NoArgsConstructor @ToString @Getter @Setter
	public static class Admin {
		/**
		 * Login ID of the initial Admin user to be created 
		 */
		public String email;
		
		/**
		 * Password of the initial Admin user to be created 
		 */		
		public String password;

	}
	
	/**
	 * Properties related to JWT token generation
	 * 
	 * @author Sanjay Patel
	 */
	@Getter @Setter
	public static class Jwt {


		/**
		 * Secret for signing JWT
		 */
		public String secret;
		
		/**
		 * Default expiration milliseconds
		 */
		public long expirationMillis = 864000000L; // 10 days
		
		/**
		 * Expiration milliseconds for short-lived tokens and cookies
		 */
		public int shortLivedMillis = 120000; // Two minutes
	}
}
