package com.naturalprogrammer.spring.lemon.auth.security.service;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

@ServiceComponent
public interface BlueTokenService extends LemonTokenService {

	String USER_CLAIM = "user";
	String AUTH_AUDIENCE = "auth";
}
