package com.github.vincemann.springrapid.core.service.context;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {
    T get() throws E;
}