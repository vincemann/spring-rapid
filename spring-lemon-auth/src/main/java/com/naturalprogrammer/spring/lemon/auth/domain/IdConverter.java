package com.naturalprogrammer.spring.lemon.auth.domain;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;

import java.io.Serializable;

@FunctionalInterface
@ServiceComponent
public interface IdConverter<ID extends Serializable> {

	ID toId(String id);
}
