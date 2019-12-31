package io.github.vincemann.generic.crud.lib.test.equalChecker;

public interface EqualChecker<T> {
    boolean isEqual(T object1, T object2);
}
