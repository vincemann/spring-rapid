package com.github.vincemann.springlemon.exceptions.handlers;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.github.fge.jsonpatch.JsonPatchException;

//@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class JsonPatchExceptionHandler extends AbstractBadRequestExceptionHandler<JsonPatchException> {

	public JsonPatchExceptionHandler() {
		
		super(JsonPatchException.class);
		log.info("Created");
	}
}
