package io.github.vincemann.spring.lemon.exceptions.handlers;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.fasterxml.jackson.core.JsonParseException;

//@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class JsonParseExceptionHandler extends AbstractBadRequestExceptionHandler<JsonParseException> {

	public JsonParseExceptionHandler() {
		
		super(JsonParseException.class);
		log.info("Created");
	}
}
