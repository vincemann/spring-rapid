package com.github.vincemann.springlemon.auth.security.service;

import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

/**
 * Generates/ Parses Email Tokens
 * Used for all actions that require email
 */
@ServiceComponent
public interface GreenTokenService extends LemonTokenService {

	String VERIFY_AUDIENCE = "verify";
	String FORGOT_PASSWORD_AUDIENCE = "forgot-password";
	String CHANGE_EMAIL_AUDIENCE = "change-email";
}
