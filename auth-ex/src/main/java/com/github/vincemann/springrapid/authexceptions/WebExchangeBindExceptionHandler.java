package com.github.vincemann.springrapid.authexceptions;

import com.github.vincemann.springrapid.auth.util.FieldErrorUtil;
import com.github.vincemann.springrapid.lemon.exceptions.AbstractExceptionHandler;
import com.github.vincemann.springrapid.exceptionsapi.FieldError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Order(Ordered.LOWEST_PRECEDENCE)
public class WebExchangeBindExceptionHandler extends AbstractExceptionHandler<WebExchangeBindException> {

	public WebExchangeBindExceptionHandler() {
		super(WebExchangeBindException.class);
	}

	@Override
	public HttpStatus getStatus(WebExchangeBindException ex) {
		return HttpStatus.UNPROCESSABLE_ENTITY;
	}


	@Override
	public Collection<FieldError> getErrors(WebExchangeBindException ex) {
		List<FieldError> errors = ex.getFieldErrors().stream()
				.map(FieldErrorUtil::of).collect(Collectors.toList());

		errors.addAll(ex.getGlobalErrors().stream()
				.map(FieldErrorUtil::of).collect(Collectors.toSet()));

		return errors;
	}

}
