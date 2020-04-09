package io.github.vincemann.springrapid.acl.model;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import java.io.Serializable;

public interface AclParentAware {
    public IdentifiableEntity<? extends Serializable> getAclParent();
}
