package com.github.vincemann.springrapid.authexceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.vincemann.springrapid.exceptionsapi.AbstractBadRequestExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.LOWEST_PRECEDENCE)
public class JsonParseExceptionHandler extends AbstractBadRequestExceptionHandler<JsonParseException> {

	public JsonParseExceptionHandler() {
		super(JsonParseException.class);
	}
}
