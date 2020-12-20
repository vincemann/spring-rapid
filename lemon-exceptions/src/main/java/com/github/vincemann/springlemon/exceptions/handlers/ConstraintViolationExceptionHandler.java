package com.github.vincemann.springlemon.exceptions.handlers;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import com.github.vincemann.springlemon.exceptions.FieldError;
import com.github.vincemann.springlemon.exceptions.util.LemonFieldErrorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

//@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class ConstraintViolationExceptionHandler extends AbstractBadRequestExceptionHandler<ConstraintViolationException> {

	public ConstraintViolationExceptionHandler() {
		super(ConstraintViolationException.class);
	}

	@Override
	public Collection<FieldError> getErrors(ConstraintViolationException ex) {
		return ex.getConstraintViolations().stream()
				.map(LemonFieldErrorUtil::of).collect(Collectors.toList());
	}


}
