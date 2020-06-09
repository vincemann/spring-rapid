package com.github.vincemann.springlemon.auth.domain;

import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.slicing.config.ServiceConfig;

import java.io.Serializable;

@FunctionalInterface
@ServiceComponent
public interface IdConverter<ID extends Serializable> {

	ID toId(String id);
}
