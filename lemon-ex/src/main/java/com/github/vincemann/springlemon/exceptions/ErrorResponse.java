package com.github.vincemann.springlemon.exceptions;

import java.util.Collection;


/**
 * Error DTO, to be sent as response body
 * in case of errors
 */

public class ErrorResponse {
	
	private String exceptionId;
	private String error;
	private String message;
	private Integer status; // We'd need it as integer in JSON serialization
	private Collection<FieldError> errors;
	
	public boolean incomplete() {
		return message == null || status == null;
	}

	public String getExceptionId() {
		return exceptionId;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public Integer getStatus() {
		return status;
	}

	public Collection<FieldError> getErrors() {
		return errors;
	}

	public void setExceptionId(String exceptionId) {
		this.exceptionId = exceptionId;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setErrors(Collection<FieldError> errors) {
		this.errors = errors;
	}

	@Override
	public String toString() {
		return "ErrorResponse{" +
				"exceptionId='" + exceptionId + '\'' +
				", error='" + error + '\'' +
				", message='" + message + '\'' +
				", status=" + status +
				", errors=" + errors +
				", incomplete=" + incomplete() +
				'}';
	}
}
