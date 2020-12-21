package com.github.vincemann.springrapid.exceptionsapi;

import java.util.Collection;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * Extend this to code an exception handler
 */
@Slf4j
public abstract class AbstractExceptionHandler<T extends Throwable> {
	

	private Class<?> exceptionClass;
	
	public AbstractExceptionHandler(Class<?> exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	public Class<?> getExceptionClass() {
		return exceptionClass;
	}
	
//	public String getExceptionId(T ex) {
//		return LemonExceptionUtils.getExceptionId(ex);
//	}

	public String getExceptionId(T ex) {
		if (ex == null)
			return null;

		return ex.getClass().getSimpleName();
	}

	public String getMessage(T ex) {
		return ex.getMessage();
	}

	public abstract HttpStatus getStatus(T ex);

	public Collection<FieldError> getErrors(T ex) {
		return null;
	}

}
