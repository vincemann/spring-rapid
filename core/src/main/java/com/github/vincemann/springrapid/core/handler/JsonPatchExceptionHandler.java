package com.github.vincemann.springrapid.core.handler;

import com.github.vincemann.springrapid.exceptionsapi.AbstractBadRequestExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.github.fge.jsonpatch.JsonPatchException;

@Order(Ordered.LOWEST_PRECEDENCE)
public class JsonPatchExceptionHandler extends AbstractBadRequestExceptionHandler<JsonPatchException> {

	public JsonPatchExceptionHandler() {
		super(JsonPatchException.class);
	}
}
