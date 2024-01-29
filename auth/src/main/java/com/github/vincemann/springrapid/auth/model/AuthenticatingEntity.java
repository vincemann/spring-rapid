package com.github.vincemann.springrapid.auth.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.io.Serializable;

public interface AuthenticatingEntity<Id extends Serializable>
        extends IdentifiableEntity<Id> {
    public String getAuthenticationName();
}
