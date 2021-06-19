package com.github.vincemann.springrapid.auth.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.io.Serializable;

public interface AuthenticatingEntity<ID extends Serializable> extends IdentifiableEntity<ID> {
    public String getAuthenticationName();
}
