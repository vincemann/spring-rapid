package com.github.vincemann.springlemon.exceptions.web;

import com.github.vincemann.springlemon.exceptions.ErrorResponse;
import com.github.vincemann.springlemon.exceptions.ErrorResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles exceptions thrown from in controllers or inner routines
 */
@RestControllerAdvice
public class LemonExceptionHandlerControllerAdvice<T extends Throwable>
{

	private final Log log = LogFactory.getLog(getClass());
	

	/**
	 * Component that actually builds the error response
	 */
	private ErrorResponseFactory<T> errorResponseFactory;
	
    public LemonExceptionHandlerControllerAdvice(ErrorResponseFactory<T> errorResponseFactory) {
		this.errorResponseFactory = errorResponseFactory;
	}


	/**
     * Handles exceptions
     */
    @RequestMapping(produces = "application/json")
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleException(T ex) throws T {
		log.debug("Handling exception", ex);
    	ErrorResponse errorResponse = errorResponseFactory.create(ex).orElseThrow(() -> {
    		log.warn("Could not compose ErrorResponse, throwing exception");
    		return ex;
		});
    	
    	// Propagate up if message or status is null
    	if (errorResponse.incomplete()){
    		log.warn("Throwing exception, bc composed ErrorResponse is incomplete: " + errorResponse);
			throw ex;
		}

    	
    	log.debug("Sending ErrorResponse to client...");
    	return ResponseEntity
				.status(HttpStatus.valueOf(errorResponse.getStatus()))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(errorResponse);
	}
}
