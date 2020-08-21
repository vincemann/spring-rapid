package com.github.vincemann.springlemon.auth.service;

import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

/**
 * Used for dealing with tokens generated for authorization purposes, usually send via HTTPS in the Authorization Header.
 */
@ServiceComponent
public interface AuthorizationTokenService extends JwtService {
	String USER_CLAIM = "user";
	String AUTH_AUDIENCE = "auth";
}
