package com.naturalprogrammer.spring.lemon.auth.domain;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.io.Serializable;
import java.util.Set;

public interface LemonUser<ID extends Serializable> extends IdentifiableEntity<ID> {

	void setEmail(String username);
	void setPassword(String password);
	Set<String> getRoles();
	String getPassword();
	void setCredentialsUpdatedMillis(long currentTimeMillis);
	ID getId();
	String getEmail();

}
