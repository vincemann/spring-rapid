package com.github.vincemann.springrapid.auth;

import com.github.vincemann.springrapid.core.CoreProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@Validated
public class AuthProperties {


	private CoreProperties coreProperties;
	public Controller controller;

	public AuthProperties(CoreProperties coreProperties) {
		this.coreProperties = coreProperties;
		this.controller = new Controller(coreProperties);
	}

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
			testTokenUrl = getUserBaseUrl() + "/test-token";
			blockUserUrl = getUserBaseUrl() + "/block";
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

		public void setCoreProperties(CoreProperties coreProperties) {
			this.coreProperties = coreProperties;
		}

		public void setUserBaseUrl(String userBaseUrl) {
			this.userBaseUrl = userBaseUrl;
		}

		public void setLoginUrl(String loginUrl) {
			this.loginUrl = loginUrl;
		}

		public void setSignupUrl(String signupUrl) {
			this.signupUrl = signupUrl;
		}

		public void setResetPasswordUrl(String resetPasswordUrl) {
			this.resetPasswordUrl = resetPasswordUrl;
		}

		public void setResetPasswordViewUrl(String resetPasswordViewUrl) {
			this.resetPasswordViewUrl = resetPasswordViewUrl;
		}

		public void setFindByContactInformationUrl(String findByContactInformationUrl) {
			this.findByContactInformationUrl = findByContactInformationUrl;
		}

		public void setChangeContactInformationUrl(String changeContactInformationUrl) {
			this.changeContactInformationUrl = changeContactInformationUrl;
		}

		public void setChangeContactInformationViewUrl(String changeContactInformationViewUrl) {
			this.changeContactInformationViewUrl = changeContactInformationViewUrl;
		}

		public void setVerifyUserUrl(String verifyUserUrl) {
			this.verifyUserUrl = verifyUserUrl;
		}

		public void setResendVerifyContactInformationMsgUrl(String resendVerifyContactInformationMsgUrl) {
			this.resendVerifyContactInformationMsgUrl = resendVerifyContactInformationMsgUrl;
		}

		public void setForgotPasswordUrl(String forgotPasswordUrl) {
			this.forgotPasswordUrl = forgotPasswordUrl;
		}

		public void setChangePasswordUrl(String changePasswordUrl) {
			this.changePasswordUrl = changePasswordUrl;
		}

		public void setRequestContactInformationChangeUrl(String requestContactInformationChangeUrl) {
			this.requestContactInformationChangeUrl = requestContactInformationChangeUrl;
		}

		public void setFetchNewAuthTokenUrl(String fetchNewAuthTokenUrl) {
			this.fetchNewAuthTokenUrl = fetchNewAuthTokenUrl;
		}

		public void setTestTokenUrl(String testTokenUrl) {
			this.testTokenUrl = testTokenUrl;
		}

		public void setBlockUserUrl(String blockUserUrl) {
			this.blockUserUrl = blockUserUrl;
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
	public static class Admin {
		/**
		 * Login ID of the initial Admin user to be created 
		 */
		public String contactInformation;
		
		/**
		 * Password of the initial Admin user to be created 
		 */
		public String password;

		public Boolean replace;

		public Admin(String contactInformation, String password) {
			this.contactInformation = contactInformation;
			this.password = password;
			this.replace = true;
		}

		public Admin(String contactInformation, String password, Boolean replace) {
			this.contactInformation = contactInformation;
			this.password = password;
			this.replace = replace;
		}

		public Admin() {
		}


		public String getContactInformation() {
			return contactInformation;
		}

		public void setContactInformation(String contactInformation) {
			this.contactInformation = contactInformation;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public Boolean getReplace() {
			return replace;
		}

		public void setReplace(Boolean replace) {
			this.replace = replace;
		}

		@Override
		public String toString() {
			return "Admin{" +
					"contactInformation='" + contactInformation + '\'' +
					", password='" + password + '\'' +
					", replace=" + replace +
					'}';
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

	public void setCoreProperties(CoreProperties coreProperties) {
		this.coreProperties = coreProperties;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public void setMaxLoginAttempts(int maxLoginAttempts) {
		this.maxLoginAttempts = maxLoginAttempts;
	}

	public void setBruteForceProtection(boolean bruteForceProtection) {
		this.bruteForceProtection = bruteForceProtection;
	}

	public void setAdmins(List<Admin> admins) {
		this.admins = admins;
	}

	public void setJwt(Jwt jwt) {
		this.jwt = jwt;
	}
}
