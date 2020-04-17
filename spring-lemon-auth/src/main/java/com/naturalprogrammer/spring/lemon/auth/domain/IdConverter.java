package com.naturalprogrammer.spring.lemon.auth.domain;

import java.io.Serializable;

@FunctionalInterface
public interface IdConverter<ID extends Serializable> {

	ID toId(String id);
}
