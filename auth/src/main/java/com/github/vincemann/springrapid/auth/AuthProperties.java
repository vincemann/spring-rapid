package com.github.vincemann.springrapid.auth;

import com.github.vincemann.springrapid.core.CoreProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Validated
@Slf4j
@Getter
@Setter
public class AuthProperties {


	private CoreProperties coreProperties;

	public AuthProperties(CoreProperties coreProperties) {
		this.coreProperties = coreProperties;
		this.controller = new Controller(coreProperties);
	}

	public Controller controller;


	@Getter
	@Setter
	public static class Controller {

		private CoreProperties coreProperties;

		public Controller(CoreProperties coreProperties) {
			this.coreProperties = coreProperties;
		}

		public String userBaseUrl;

		public String loginUrl;

		public String signupUrl;
		public String resetPasswordUrl;
		public String resetPasswordViewUrl;
		public String findByContactInformationUrl;
		public String changeContactInformationUrl;
		public String changeContactInformationViewUrl;
		public String verifyUserUrl;
		public String resendVerifyContactInformationMsgUrl;
		public String forgotPasswordUrl;
		public String changePasswordUrl;
		public String requestContactInformationChangeUrl;
		public String fetchNewAuthTokenUrl;
		public String testTokenUrl;

		public String getUserBaseUrl() {
			return coreProperties.getBaseUrl()+"/user";
		}

		public String getLoginUrl() {
			return coreProperties.getBaseUrl()+"/login";
		}

		public String getSignupUrl() {
			return getUserBaseUrl() +"/signup";
		}

		public String getResetPasswordUrl() {
			return getUserBaseUrl() +"/reset-password";
		}

		public String getResetPasswordViewUrl() {
			return getUserBaseUrl() +"/reset-password-view";
		}

		public String getFindByContactInformationUrl() {
			return getUserBaseUrl() +"/find-by-ci";
		}

		public String getChangeContactInformationUrl() {
			return getUserBaseUrl() +"/change-ci";
		}

		public String getChangeContactInformationViewUrl() {
			return getUserBaseUrl() +"/change-ci";
		}

		public String getVerifyUserUrl() {
			return getUserBaseUrl() +"/verify";
		}

		public String getResendVerifyContactInformationMsgUrl() {
			return getUserBaseUrl() +"/resend-verify";
		}

		public String getForgotPasswordUrl() {
			return getUserBaseUrl() +"/forgot-password";
		}

		public String getChangePasswordUrl() {
			return getUserBaseUrl() +"/change-password";
		}

		public String getRequestContactInformationChangeUrl() {
			return getUserBaseUrl() +"/request-change-ci";
		}

		public String getFetchNewAuthTokenUrl() {
			return getUserBaseUrl() +"/new-token";
		}

		public String getTestTokenUrl() {
			return getUserBaseUrl() + "test-token";
		}
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
		@NotBlank
		@Email
		public String contactInformation;
		
		/**
		 * Password of the initial Admin user to be created 
		 */
		@NotBlank
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
