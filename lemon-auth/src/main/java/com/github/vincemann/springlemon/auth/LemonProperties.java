package com.github.vincemann.springlemon.auth;

import com.github.vincemann.springrapid.core.RapidCoreProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Validated
@Slf4j
public class LemonProperties {

	@Value("${rapid.core.baseUrl}")
	private static String baseUrl;

	public UserController userController = new UserController();


	public static class UserController {

		public String userBaseUrl = baseUrl+"/user";

		public String loginUrl = baseUrl+"/login";
		public String pingUrl = baseUrl+"/ping";
		public String contextUrl = baseUrl+"/context";

		public String signupUrl = userBaseUrl +"/signup";
		public String resetPasswordUrl = userBaseUrl +"/reset-password";
		public String fetchByEmailUrl = userBaseUrl +"/fetch-by-email";
		public String changeEmailUrl = userBaseUrl +"/change-email";
		public String verifyUserUrl = userBaseUrl +"/verify";
		public String resendVerificationEmailUrl = userBaseUrl +"/resend-verification-email";
		public String forgotPasswordUrl = userBaseUrl +"/forgot-password";
		public String changePasswordUrl = userBaseUrl +"/change-password";
		public String requestEmailChangeUrl = userBaseUrl +"/request-email-change";
		public String newAuthTokenUrl = userBaseUrl +"/new-auth-token";

		public String getUserBaseUrl() {
			return userBaseUrl;
		}

		public void setUserBaseUrl(String userBaseUrl) {
			this.userBaseUrl = userBaseUrl;
		}

		public String getLoginUrl() {
			return loginUrl;
		}

		public void setLoginUrl(String loginUrl) {
			this.loginUrl = loginUrl;
		}

		public String getPingUrl() {
			return pingUrl;
		}

		public void setPingUrl(String pingUrl) {
			this.pingUrl = pingUrl;
		}

		public String getContextUrl() {
			return contextUrl;
		}

		public void setContextUrl(String contextUrl) {
			this.contextUrl = contextUrl;
		}

		public String getSignupUrl() {
			return signupUrl;
		}

		public void setSignupUrl(String signupUrl) {
			this.signupUrl = signupUrl;
		}

		public String getResetPasswordUrl() {
			return resetPasswordUrl;
		}

		public void setResetPasswordUrl(String resetPasswordUrl) {
			this.resetPasswordUrl = resetPasswordUrl;
		}

		public String getFetchByEmailUrl() {
			return fetchByEmailUrl;
		}

		public void setFetchByEmailUrl(String fetchByEmailUrl) {
			this.fetchByEmailUrl = fetchByEmailUrl;
		}

		public String getChangeEmailUrl() {
			return changeEmailUrl;
		}

		public void setChangeEmailUrl(String changeEmailUrl) {
			this.changeEmailUrl = changeEmailUrl;
		}

		public String getVerifyUserUrl() {
			return verifyUserUrl;
		}

		public void setVerifyUserUrl(String verifyUserUrl) {
			this.verifyUserUrl = verifyUserUrl;
		}

		public String getResendVerificationEmailUrl() {
			return resendVerificationEmailUrl;
		}

		public void setResendVerificationEmailUrl(String resendVerificationEmailUrl) {
			this.resendVerificationEmailUrl = resendVerificationEmailUrl;
		}

		public String getForgotPasswordUrl() {
			return forgotPasswordUrl;
		}

		public void setForgotPasswordUrl(String forgotPasswordUrl) {
			this.forgotPasswordUrl = forgotPasswordUrl;
		}

		public String getChangePasswordUrl() {
			return changePasswordUrl;
		}

		public void setChangePasswordUrl(String changePasswordUrl) {
			this.changePasswordUrl = changePasswordUrl;
		}

		public String getRequestEmailChangeUrl() {
			return requestEmailChangeUrl;
		}

		public void setRequestEmailChangeUrl(String requestEmailChangeUrl) {
			this.requestEmailChangeUrl = requestEmailChangeUrl;
		}

		public String getNewAuthTokenUrl() {
			return newAuthTokenUrl;
		}

		public void setNewAuthTokenUrl(String newAuthTokenUrl) {
			this.newAuthTokenUrl = newAuthTokenUrl;
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

	public UserController getUserController() {
		return userController;
	}

	public void setUserController(UserController userController) {
		this.userController = userController;
	}
}
