package io.github.vincemann.generic.crud.lib.test.deepEqualChecker;

public interface EqualChecker<T> {
    boolean isEqual(T object1, T object2);
}
