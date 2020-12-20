package com.github.vincemann.springlemon.exceptions.handlers;

import java.util.Collection;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.springlemon.exceptions.FieldError;
import com.github.vincemann.springlemon.exceptions.util.LemonExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * Extend this to code an exception handler
 */
@Slf4j
public abstract class AbstractExceptionHandler<T extends Throwable>
		implements AopLoggable {
	

	private Class<?> exceptionClass;
	
	public AbstractExceptionHandler(Class<?> exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	public Class<?> getExceptionClass() {
		return exceptionClass;
	}
	
	public String getExceptionId(T ex) {
		return LemonExceptionUtils.getExceptionId(ex);
	}

	public String getMessage(T ex) {
		return ex.getMessage();
	}

	public abstract HttpStatus getStatus(T ex);

	public Collection<FieldError> getErrors(T ex) {
		return null;
	}

}
