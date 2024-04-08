package com.github.vincemann.springrapid.lemon.exceptions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpStatus;

/**
 * Default implementation.
 * Given an exception, builds an {@link ErrorResponse}.
 *
 */
public class LemonErrorResponseFactory<T extends Throwable>
		implements ErrorResponseFactory<T> {
	

	private final Map<Class<?>, AbstractExceptionHandler<T>> handlers;
	
	public LemonErrorResponseFactory(List<AbstractExceptionHandler<T>> handlers) {
		// save to map ordered by @Ordered interface + ExceptionClass as key
		this.handlers = handlers.stream().collect(
	            Collectors.toMap(AbstractExceptionHandler::getExceptionClass,
	            		Function.identity(), (handler1, handler2) -> {
	            			
	            			return AnnotationAwareOrderComparator
	            					.INSTANCE.compare(handler1, handler2) < 0 ?
	            					handler1 : handler2;
	            		}));
		

	}

	/**
	 * Given an exception, finds a handler for 
	 * building the response and uses that to build and return the response
	 */
	@Override
	public Optional<ErrorResponse> create(T ex) {

		AbstractExceptionHandler<T> handler = null;
		
        // find a handler for the exception
        // if no handler is found,
        // loop into for its cause (ex.getCause())

		while (ex != null) {
			
			handler = handlers.get(ex.getClass());
			
			if (handler != null) // found a handler
				break;
			
			ex = (T) ex.getCause();			
		}
        
        if (handler != null) // a handler is found    	
        	return Optional.of(create(handler,ex));
        
        return Optional.empty();
	}

	protected ErrorResponse create(AbstractExceptionHandler<T> handler, T ex){
		ErrorResponse errorResponse = new ErrorResponse();

		errorResponse.setExceptionId(handler.getExceptionId(ex));
		errorResponse.setMessage(handler.getMessage(ex));

		HttpStatus status = handler.getStatus(ex);
		if (status != null) {
			errorResponse.setStatus(status.value());
			errorResponse.setError(status.getReasonPhrase());
		}

		errorResponse.setErrors(handler.getErrors(ex));
		return errorResponse;
	}
}
