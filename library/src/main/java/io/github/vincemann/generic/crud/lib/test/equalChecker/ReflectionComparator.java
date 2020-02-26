package io.github.vincemann.generic.crud.lib.test.equalChecker;

public interface ReflectionComparator<T> {
    boolean isEqual(T expected, T actual);
}
