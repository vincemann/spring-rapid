package com.github.vincemann.springrapid.auth.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

import com.github.vincemann.springrapid.exceptionsapi.AbstractExceptionHandler;

//@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class BadCredentialsExceptionHandler extends AbstractExceptionHandler<BadCredentialsException> {
	
	public BadCredentialsExceptionHandler() {
		
		super(BadCredentialsException.class);

	}
	
	@Override
	public HttpStatus getStatus(BadCredentialsException ex) {
		return HttpStatus.UNAUTHORIZED;
	}
}
