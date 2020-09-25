package com.github.vincemann.springlemon.exceptions.handlers;

import java.util.Collection;

import com.github.vincemann.springlemon.exceptions.LemonFieldError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.support.WebExchangeBindException;

//@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class WebExchangeBindExceptionHandler extends AbstractValidationExceptionHandler<WebExchangeBindException> {

	public WebExchangeBindExceptionHandler() {
		
		super(WebExchangeBindException.class);
		log.info("Created");
	}
	
	@Override
	public Collection<LemonFieldError> getErrors(WebExchangeBindException ex) {
		return LemonFieldError.getErrors(ex);
	}
}
