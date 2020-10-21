package com.github.vincemann.springrapid.auth.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.github.vincemann.springlemon.exceptions.handlers.AbstractExceptionHandler;

//@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class UsernameNotFoundExceptionHandler extends AbstractExceptionHandler<UsernameNotFoundException> {
	
	public UsernameNotFoundExceptionHandler() {
		
		super(UsernameNotFoundException.class);

	}
	
	@Override
	public HttpStatus getStatus(UsernameNotFoundException ex) {
		return HttpStatus.UNAUTHORIZED;
	}
}
