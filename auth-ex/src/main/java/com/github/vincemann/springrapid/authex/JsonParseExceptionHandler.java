package com.github.vincemann.springrapid.authex;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.vincemann.springrapid.lemon.exceptions.AbstractBadRequestExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.LOWEST_PRECEDENCE)
public class JsonParseExceptionHandler extends AbstractBadRequestExceptionHandler<JsonParseException> {

	public JsonParseExceptionHandler() {
		super(JsonParseException.class);
	}
}
