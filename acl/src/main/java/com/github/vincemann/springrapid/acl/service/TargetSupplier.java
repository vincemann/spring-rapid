package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.util.Collection;

@FunctionalInterface
public interface TargetSupplier<S,T> {

    public Collection<T> get(S entity);
}
