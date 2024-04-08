package com.github.vincemann.springrapid.lemon.exceptions;


/**
 * Holds a field or form error
 * 
 * @author Sanjay Patel
 */
public class FieldError {
	
	// Name of the field. Null in case of a form level error. 
	private String field;
	
	// Error code. Typically the I18n message-code.
	private String code;
	
	// Error message
	private String message;

	public FieldError() {
	}

	public FieldError(String field, String code, String message) {
		this.field = field;
		this.code = code;
		this.message = message;
	}

	public String getField() {
		return field;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
