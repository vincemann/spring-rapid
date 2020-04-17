package com.naturalprogrammer.spring.lemon.auth.security.service;

/**
 * Generates/ Parses Email Tokens
 * Used for all actions that require email
 */
public interface GreenTokenService extends LemonTokenService {

	String VERIFY_AUDIENCE = "verify";
	String FORGOT_PASSWORD_AUDIENCE = "forgot-password";
	String CHANGE_EMAIL_AUDIENCE = "change-email";
}
