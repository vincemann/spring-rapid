package com.github.vincemann.springrapid.acl.service;

import java.util.Collection;

@FunctionalInterface
public interface TargetSupplier<S,T> {

    public Collection<T> get(S entity);
}
