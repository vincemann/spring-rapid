package com.github.vincemann.springrapid.auth.domain;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.sun.xml.bind.v2.model.core.ID;

import java.io.Serializable;

public interface AuthenticatingEntity<ID extends Serializable> extends IdentifiableEntity<ID> {
    public String getAuthenticationString();
}
