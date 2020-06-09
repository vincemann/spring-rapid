package com.github.vincemann.springlemon.auth.security.service;

import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

@ServiceComponent
public interface BlueTokenService extends LemonTokenService {

	String USER_CLAIM = "user";
	String AUTH_AUDIENCE = "auth";
}
