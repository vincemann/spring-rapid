package com.github.vincemann.springlemon.auth.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import com.github.vincemann.springlemon.exceptions.handlers.AbstractExceptionHandler;

//@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class AccessDeniedExceptionHandler extends AbstractExceptionHandler<AccessDeniedException> {
	
	public AccessDeniedExceptionHandler() {
		
		super(AccessDeniedException.class);
		log.info("Created");
	}
	
	@Override
	public HttpStatus getStatus(AccessDeniedException ex) {
		return HttpStatus.FORBIDDEN;
	}
}
