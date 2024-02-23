package com.github.vincemann.springrapid.auth;

import com.github.vincemann.springrapid.auth.service.val.ValidContactInformation;
import com.github.vincemann.springrapid.auth.service.val.ValidPassword;
import com.github.vincemann.springrapid.core.CoreProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
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
			initUrls();
		}

		// init with default values
		public void initUrls(){
			userBaseUrl = coreProperties.getBaseUrl()+"/user";
			loginUrl = coreProperties.getBaseUrl()+"/login";

			signupUrl = getUserBaseUrl() +"/signup";
			resetPasswordUrl = getUserBaseUrl() +"/reset-password";
			resetPasswordViewUrl = getUserBaseUrl() +"/reset-password-view";
			findByContactInformationUrl = getUserBaseUrl() +"/find-by-ci";
			changeContactInformationUrl = getUserBaseUrl() +"/change-ci";
			changeContactInformationViewUrl = getUserBaseUrl() +"/change-ci-view";
			verifyUserUrl = getUserBaseUrl() +"/verify";
			resendVerifyContactInformationMsgUrl = getUserBaseUrl() +"/resend-verify";
			forgotPasswordUrl = getUserBaseUrl() +"/forgot-password";
			changePasswordUrl = getUserBaseUrl() +"/change-password";
			requestContactInformationChangeUrl = getUserBaseUrl() +"/request-change-ci";
			fetchNewAuthTokenUrl = getUserBaseUrl() +"/new-token";
			testTokenUrl = getUserBaseUrl() + "test-token";
			blockUserUrl = getUserBaseUrl() + "block";
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
		public String blockUserUrl;

		public String getUserBaseUrl() {
			return userBaseUrl;
		}

		public String getLoginUrl() {
			return loginUrl;
		}

		public String getSignupUrl() {
			return signupUrl;
		}

		public String getResetPasswordUrl() {
			return resetPasswordUrl;
		}

		public String getResetPasswordViewUrl() {
			return resetPasswordViewUrl;
		}

		public String getFindByContactInformationUrl() {
			return findByContactInformationUrl;
		}

		public String getChangeContactInformationUrl() {
			return changeContactInformationUrl;
		}

		public String getChangeContactInformationViewUrl() {
			return changeContactInformationViewUrl;
		}

		public String getVerifyUserUrl() {
			return verifyUserUrl;
		}

		public String getResendVerifyContactInformationMsgUrl() {
			return resendVerifyContactInformationMsgUrl;
		}

		public String getForgotPasswordUrl() {
			return forgotPasswordUrl;
		}

		public String getChangePasswordUrl() {
			return changePasswordUrl;
		}

		public String getRequestContactInformationChangeUrl() {
			return requestContactInformationChangeUrl;
		}

		public String getFetchNewAuthTokenUrl() {
			return fetchNewAuthTokenUrl;
		}

		public String getTestTokenUrl() {
			return testTokenUrl;
		}

		public String getBlockUserUrl() {
			return blockUserUrl;
		}
	}

	public CoreProperties getCoreProperties() {
		return coreProperties;
	}


	@Min(1)
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
		@ValidContactInformation
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

	public Controller getController() {
		return controller;
	}

	public int getMaxLoginAttempts() {
		return maxLoginAttempts;
	}

	public boolean isBruteForceProtection() {
		return bruteForceProtection;
	}

	public List<Admin> getAdmins() {
		return admins;
	}

	public Jwt getJwt() {
		return jwt;
	}
}
