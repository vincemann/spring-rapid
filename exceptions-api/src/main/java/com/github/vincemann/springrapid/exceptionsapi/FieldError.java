package com.github.vincemann.springrapid.exceptionsapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Holds a field or form error
 * 
 * @author Sanjay Patel
 */
@Getter @AllArgsConstructor @ToString
public class FieldError {
	
	// Name of the field. Null in case of a form level error. 
	private String field;
	
	// Error code. Typically the I18n message-code.
	private String code;
	
	// Error message
	private String message;

}
