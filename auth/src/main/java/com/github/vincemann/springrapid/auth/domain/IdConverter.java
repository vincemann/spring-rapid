package com.github.vincemann.springrapid.auth.domain;

import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

import java.io.Serializable;

@FunctionalInterface
@ServiceComponent
public interface IdConverter<ID extends Serializable> {

	ID toId(String id);
}
