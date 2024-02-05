package com.github.vincemann.springrapid.auth.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import com.github.vincemann.springrapid.exceptionsapi.AbstractExceptionHandler;

@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class AccessDeniedExceptionHandler extends AbstractExceptionHandler<AccessDeniedException> {
	
	public AccessDeniedExceptionHandler() {
		super(AccessDeniedException.class);
	}
	
	@Override
	public HttpStatus getStatus(AccessDeniedException ex) {
		return HttpStatus.FORBIDDEN;
	}
}
