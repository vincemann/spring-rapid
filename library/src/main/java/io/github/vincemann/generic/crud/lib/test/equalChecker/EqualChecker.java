package io.github.vincemann.generic.crud.lib.test.equalChecker;

public interface EqualChecker<T> {
    boolean isEqual(T request, T updated);
}
