package io.github.vincemann.generic.crud.lib.test.deepCompare;

public interface ReflectionComparator<T> {
    boolean isEqual(T expected, T actual);
}
