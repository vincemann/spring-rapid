package com.github.vincemann.springlemon.exceptions.web;

import com.github.vincemann.springlemon.exceptions.ErrorResponseFactory;
import com.github.vincemann.springlemon.exceptions.LemonErrorResponseFactory;
import com.github.vincemann.springlemon.exceptions.util.LemonExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * Used for handling exceptions that can't be handled by
 * <code>DefaultExceptionHandlerControllerAdvice</code>,
 * e.g. exceptions thrown in filters.
 */
@Slf4j
public class LemonErrorAttributes<T extends Throwable> extends DefaultErrorAttributes {
	

	static final String HTTP_STATUS_KEY = "httpStatus";
	
	private ErrorResponseFactory<T> errorResponseFactory;
	
    public LemonErrorAttributes(ErrorResponseFactory<T> errorResponseFactory) {
		this.errorResponseFactory = errorResponseFactory;
	}
	
    /**
     * Calls the base class and then adds our details
     */
	@Override
	public Map<String, Object> getErrorAttributes(WebRequest request,
			boolean includeStackTrace) {
			
		Map<String, Object> errorAttributes =
				super.getErrorAttributes(request, includeStackTrace);
		
		addLemonErrorDetails(errorAttributes, request);
		
		return errorAttributes;
	}

	/**
     * Handles exceptions
     */
	@SuppressWarnings("unchecked")
	protected void addLemonErrorDetails(
			Map<String, Object> errorAttributes, WebRequest request) {
		
		Throwable ex = getError(request);
		
		errorResponseFactory.create((T)ex).ifPresent(errorResponse -> {
			
			// check for null - errorResponse may have left something for the DefaultErrorAttributes
			
			if (errorResponse.getExceptionId() != null)
				errorAttributes.put("exceptionId", errorResponse.getExceptionId());

			if (errorResponse.getMessage() != null)
				errorAttributes.put("message", errorResponse.getMessage());
			
			Integer status = errorResponse.getStatus();
			
			if (status != null) {
				errorAttributes.put(HTTP_STATUS_KEY, status); // a way to pass response status to LemonErrorController
				errorAttributes.put("status", status);
				errorAttributes.put("error", errorResponse.getError());
			}

			if (errorResponse.getErrors() != null)
				errorAttributes.put("errors", errorResponse.getErrors());			
		});
		
		if (errorAttributes.get("exceptionId") == null)
			errorAttributes.put("exceptionId", LemonExceptionUtils.getExceptionId(ex));
	}
}
