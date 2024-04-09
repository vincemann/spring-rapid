package com.github.vincemann.springrapid.auth;

import org.springframework.stereotype.Component;

import java.io.Serializable;

public interface IdConverter<ID extends Serializable> {

	ID toId(String id);

	Class<? extends Serializable> getIdType();
}
