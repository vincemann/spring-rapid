package com.github.vincemann.springrapid.core.handler;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;


import com.github.vincemann.springrapid.core.util.FieldErrorUtil;
import com.github.vincemann.springrapid.exceptionsapi.AbstractBadRequestExceptionHandler;
import com.github.vincemann.springrapid.exceptionsapi.FieldError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class ConstraintViolationExceptionHandler extends AbstractBadRequestExceptionHandler<ConstraintViolationException> {

	public ConstraintViolationExceptionHandler() {
		super(ConstraintViolationException.class);
	}

	@Override
	public Collection<FieldError> getErrors(ConstraintViolationException ex) {
		return ex.getConstraintViolations().stream()
				.map(FieldErrorUtil::of).collect(Collectors.toList());
	}


}
