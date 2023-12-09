package com.github.vincemann.springrapid.acl.model;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.io.Serializable;

public interface AclParentAware extends AopLoggable {
    public IdentifiableEntity<? extends Serializable> getAclParent();
}
