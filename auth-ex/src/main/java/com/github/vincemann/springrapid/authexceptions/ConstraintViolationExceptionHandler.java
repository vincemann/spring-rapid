package com.github.vincemann.springrapid.authexceptions;

import com.github.vincemann.springrapid.auth.util.FieldErrorUtil;
import com.github.vincemann.springrapid.exceptionsapi.AbstractBadRequestExceptionHandler;
import com.github.vincemann.springrapid.exceptionsapi.FieldError;
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
