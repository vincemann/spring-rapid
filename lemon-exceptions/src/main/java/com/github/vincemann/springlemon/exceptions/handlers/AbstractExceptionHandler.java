package com.github.vincemann.springlemon.exceptions.handlers;

import java.util.Collection;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springlemon.exceptions.ErrorResponse;
import com.github.vincemann.springlemon.exceptions.LemonFieldError;
import com.github.vincemann.springlemon.exceptions.util.LemonExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * Extend this to code an exception handler
 */
@Slf4j
public abstract class AbstractExceptionHandler<T extends Throwable> implements AopLoggable {
	

	private Class<?> exceptionClass;
	
	public AbstractExceptionHandler(Class<?> exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	public Class<?> getExceptionClass() {
		return exceptionClass;
	}
	
	protected String getExceptionId(T ex) {
		return LemonExceptionUtils.getExceptionId(ex);
	}

	protected String getMessage(T ex) {
		return ex.getMessage();
	}
	
	protected HttpStatus getStatus(T ex) {
		return null;
	}
	
	protected Collection<LemonFieldError> getErrors(T ex) {
		return null;
	}

	@LogInteraction
	public ErrorResponse getErrorResponse(T ex) {
    	
		ErrorResponse errorResponse = new ErrorResponse();
		
		errorResponse.setExceptionId(getExceptionId(ex));
		errorResponse.setMessage(getMessage(ex));
		
		HttpStatus status = getStatus(ex);
		if (status != null) {
			errorResponse.setStatus(status.value());
			errorResponse.setError(status.getReasonPhrase());
		}
		
		errorResponse.setErrors(getErrors(ex));
		return errorResponse;
	}
}
