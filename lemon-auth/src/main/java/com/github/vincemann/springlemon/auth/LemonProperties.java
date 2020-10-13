package com.github.vincemann.springlemon.auth;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lemon Properties
 * 
 * @author Sanjay Patel
 */
@Validated
@Slf4j
//@Component
public class LemonProperties {
	

    public LemonProperties() {

	}

	public Controller controller = new Controller();

	@Getter
	@Setter
	public static class Controller{

		public Endpoints endpoints = new Endpoints();

		@Getter
		@Setter
    	public static class Endpoints{
    		public String login = "login";
    		public String signup = "signup";
    		public String resetPassword = "resetPassword";
    		public String fetchByEmail = "fetchByEmail";
    		public String changeEmail = "changeEmail";
    		public String verifyUser = "verifyUser";
		}
	}


	/**
	 * Client web application's base URL.
	 * Used in the verification link mailed to the users, etc.
	 */
    public String applicationUrl = "http://localhost:9000";
    
//	/**
//	 * The default URL to redirect to after
//	 * a user logs in using OAuth2/OpenIDConnect
//	 */
//    public String oauth2AuthenticationSuccessUrl = "http://localhost:9000/social-login-success?token=";

//	/**
//	 * URL of the login endpoint
//	 * e.g. POST /api/core/login
//	 */
//    public String loginUrl = "/api/core/login";

    /**
	 * Recaptcha related properties
	 */
	public Recaptcha recaptcha = new Recaptcha();
	
    /**
	 * CORS related properties
	 */
	public Cors cors = new Cors();

    /**
	 * Properties related to the initial Admins to be created
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
	public static class Recaptcha {
    	
		/**
         * Google ReCaptcha Site Key
         */
    	public String sitekey;
    	        
        /**
         * Google ReCaptcha Secret Key
         */
    	public String secretkey;

		public String getSitekey() {
			return sitekey;
		}

		public void setSitekey(String sitekey) {
			this.sitekey = sitekey;
		}

		public String getSecretkey() {
			return secretkey;
		}

		public void setSecretkey(String secretkey) {
			this.secretkey = secretkey;
		}
	}
	
	
    /**
     * CORS configuration related properties
     */
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

		public String[] getAllowedOrigins() {
			return allowedOrigins;
		}

		public void setAllowedOrigins(String[] allowedOrigins) {
			this.allowedOrigins = allowedOrigins;
		}

		public String[] getAllowedMethods() {
			return allowedMethods;
		}

		public void setAllowedMethods(String[] allowedMethods) {
			this.allowedMethods = allowedMethods;
		}

		public String[] getAllowedHeaders() {
			return allowedHeaders;
		}

		public void setAllowedHeaders(String[] allowedHeaders) {
			this.allowedHeaders = allowedHeaders;
		}

		public String[] getExposedHeaders() {
			return exposedHeaders;
		}

		public void setExposedHeaders(String[] exposedHeaders) {
			this.exposedHeaders = exposedHeaders;
		}

		public long getMaxAge() {
			return maxAge;
		}

		public void setMaxAge(long maxAge) {
			this.maxAge = maxAge;
		}
	}

	
	/**
	 * Properties regarding the initial Admin user to be created
	 * 
	 * @author Sanjay Patel
	 */
	@AllArgsConstructor @NoArgsConstructor @ToString
	public static class Admin {
		/**
		 * Login ID of the initial Admin user to be created 
		 */
		public String email;
		
		/**
		 * Password of the initial Admin user to be created 
		 */		
		public String password;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
	
	/**
	 * Properties related to JWT token generation
	 * 
	 * @author Sanjay Patel
	 */
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

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public long getExpirationMillis() {
			return expirationMillis;
		}

		public void setExpirationMillis(long expirationMillis) {
			this.expirationMillis = expirationMillis;
		}

		public int getShortLivedMillis() {
			return shortLivedMillis;
		}

		public void setShortLivedMillis(int shortLivedMillis) {
			this.shortLivedMillis = shortLivedMillis;
		}
	}


	public String getApplicationUrl() {
		return applicationUrl;
	}

	public void setApplicationUrl(String applicationUrl) {
		this.applicationUrl = applicationUrl;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public Recaptcha getRecaptcha() {
		return recaptcha;
	}

	public void setRecaptcha(Recaptcha recaptcha) {
		this.recaptcha = recaptcha;
	}

	public Cors getCors() {
		return cors;
	}

	public void setCors(Cors cors) {
		this.cors = cors;
	}

	public List<Admin> getAdmins() {
		return admins;
	}

	public void setAdmins(List<Admin> admins) {
		this.admins = admins;
	}

	public Map<String, Object> getShared() {
		return shared;
	}

	public void setShared(Map<String, Object> shared) {
		this.shared = shared;
	}

	public Jwt getJwt() {
		return jwt;
	}

	public void setJwt(Jwt jwt) {
		this.jwt = jwt;
	}


}
