package com.github.vincemann.springlemon.exceptions.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.github.fge.jsonpatch.JsonPatchException;

//@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class JsonPatchExceptionHandler extends AbstractBadRequestExceptionHandler<JsonPatchException> {

	public JsonPatchExceptionHandler() {
		
		super(JsonPatchException.class);
		log.info("Created");
	}
}
