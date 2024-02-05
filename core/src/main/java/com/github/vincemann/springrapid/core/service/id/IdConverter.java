package com.github.vincemann.springrapid.core.service.id;

import org.springframework.stereotype.Component;

import java.io.Serializable;

public interface IdConverter<ID extends Serializable> {

	ID toId(String id);

	ID getUnknownId();
	Class<? extends Serializable> getIdType();
}
