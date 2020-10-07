package com.github.vincemann.springlemon.exceptions.handlers;

import java.util.Collection;

import com.github.vincemann.springlemon.exceptions.LemonFieldError;
import com.github.vincemann.springlemon.exceptions.MultiErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

//@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class MultiErrorExceptionHandler extends AbstractExceptionHandler<MultiErrorException> {

	public MultiErrorExceptionHandler() {
		
		super(MultiErrorException.class);

	}
	
	@Override
	public String getExceptionId(MultiErrorException ex) {
		
		if (ex.getExceptionId() == null)
			return super.getExceptionId(ex);
		
		return ex.getExceptionId();
	}

	@Override
	public String getMessage(MultiErrorException ex) {
		return ex.getMessage();
	}

	@Override
	public HttpStatus getStatus(MultiErrorException ex) {
		return ex.getStatus();
	}
	
	@Override
	public Collection<LemonFieldError> getErrors(MultiErrorException ex) {
		return ex.getErrors();
	}
}
