package com.github.vincemann.springrapid.authex;

import com.github.vincemann.springrapid.lemon.exceptions.AbstractBadRequestExceptionHandler;
import com.github.vincemann.springrapid.lemon.exceptions.FieldError;
import com.github.vincemann.springrapid.lemon.exceptions.FieldErrorUtil;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import jakarta.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.stream.Collectors;

@Order(Ordered.LOWEST_PRECEDENCE)
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
