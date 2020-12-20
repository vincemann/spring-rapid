package com.github.vincemann.springlemon.exceptions.handlers;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.vincemann.springlemon.exceptions.FieldError;
import com.github.vincemann.springlemon.exceptions.util.LemonFieldErrorUtil;
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
	public Collection<FieldError> getErrors(WebExchangeBindException ex) {
		List<FieldError> errors = ex.getFieldErrors().stream()
				.map(LemonFieldErrorUtil::of).collect(Collectors.toList());

		errors.addAll(ex.getGlobalErrors().stream()
				.map(LemonFieldErrorUtil::of).collect(Collectors.toSet()));

		return errors;
	}

}
