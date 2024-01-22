package com.github.vincemann.springrapid.sync.service;

@FunctionalInterface
public interface EqualsMethod<E> {
    boolean equals(E first, E second);
}
