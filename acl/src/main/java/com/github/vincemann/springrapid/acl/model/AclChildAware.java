package com.github.vincemann.springrapid.acl.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.io.Serializable;

public interface AclChildAware {
    public IdentifiableEntity<? extends Serializable> getAclChild();
}
