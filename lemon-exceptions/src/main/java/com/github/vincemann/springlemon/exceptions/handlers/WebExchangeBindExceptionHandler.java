package com.github.vincemann.springlemon.exceptions.handlers;

import java.util.Collection;

import com.github.vincemann.springlemon.exceptions.LemonFieldError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

//@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class WebExchangeBindExceptionHandler extends AbstractExceptionHandler<WebExchangeBindException> {

	public WebExchangeBindExceptionHandler() {
		
		super(WebExchangeBindException.class);

	}

	@Override
	public HttpStatus getStatus(WebExchangeBindException ex) {
		return UNPROCESSABLE_ENTITY;
	}

	@Override
	public Collection<LemonFieldError> getErrors(WebExchangeBindException ex) {
		return LemonFieldError.getErrors(ex);
	}
}
