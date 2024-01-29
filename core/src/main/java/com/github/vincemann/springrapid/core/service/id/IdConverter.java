package com.github.vincemann.springrapid.core.service.id;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;

import java.io.Serializable;

@ServiceComponent
public interface IdConverter<ID extends Serializable> {

	ID toId(String id);
	Class<? extends Serializable> getIdType();
}
