package com.github.vincemann.springrapid.acl.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import java.io.Serializable;

public interface AclParentAware {
    public IdentifiableEntity<? extends Serializable> getAclParent();
}
