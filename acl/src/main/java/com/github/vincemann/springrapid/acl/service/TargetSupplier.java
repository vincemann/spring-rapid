package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.util.Collection;

@FunctionalInterface
public interface TargetSupplier<T extends IdentifiableEntity<?>> {

    public Collection<? extends IdentifiableEntity<?>> get(T entity);
}
