package com.github.vincemann.springrapid.authex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.lemon.exceptions.AbstractBadRequestExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.LOWEST_PRECEDENCE)
public class JsonProcessingExceptionHandler extends AbstractBadRequestExceptionHandler<JsonProcessingException> {

	public JsonProcessingExceptionHandler() {
		super(JsonProcessingException.class);
	}
}
