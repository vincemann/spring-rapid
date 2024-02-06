package com.github.vincemann.springrapid.auth;

import com.github.vincemann.springrapid.core.CoreProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
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
		public String fetchByContactInformationUrl = userBaseUrl +"/fetch-by-contactInformation";
		public String changeContactInformationUrl = userBaseUrl +"/change-contactInformation";
		public String changeContactInformationViewUrl = userBaseUrl +"/change-contactInformation-view";
		public String verifyUserUrl = userBaseUrl +"/verify";
		public String resendVerifyContactInformationMsgUrl = userBaseUrl +"/resend-verify-contactInformation-msg";
		public String forgotPasswordUrl = userBaseUrl +"/forgot-password";
		public String changePasswordUrl = userBaseUrl +"/change-password";
		public String requestContactInformationChangeUrl = userBaseUrl +"/request-contactInformation-change";
		public String fetchNewAuthTokenUrl = userBaseUrl +"/fetch-new-auth-token";

	}

	public CoreProperties getCoreProperties() {
		return coreProperties;
	}


	public int maxLoginAttempts = 25;

	public boolean bruteForceProtection = false;


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



	/**
	 * Properties regarding the initial Admin user to be created
	 * 
	 * @author Sanjay Patel
	 * @modifiedBy vincemann
	 */
	@AllArgsConstructor @NoArgsConstructor @ToString @Getter @Setter
	public static class Admin {
		/**
		 * Login ID of the initial Admin user to be created 
		 */
		public String contactInformation;
		
		/**
		 * Password of the initial Admin user to be created 
		 */		
		public String password;

		public Admin(String contactInformation, String password) {
			this.contactInformation = contactInformation;
			this.password = password;
			this.replace = true;
		}

		public Boolean replace;

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
