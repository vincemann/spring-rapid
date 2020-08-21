package com.github.vincemann.springlemon.auth.service;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

/**
 * Used for dealing with tokens generated for authorization purposes, usually send via HTTPS in the Authorization Header.
 */
@ServiceComponent
public interface AuthorizationTokenService extends JwtService {
	/**
	 * claim key to retrieve {@link AbstractUser#getId()}
	 */
	String USER_EMAIL_CLAIM = "user-id";
	String AUTH_AUDIENCE = "auth";
}
