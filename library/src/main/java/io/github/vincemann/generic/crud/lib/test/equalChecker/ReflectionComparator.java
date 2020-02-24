package io.github.vincemann.generic.crud.lib.test.equalChecker;

public interface FuzzyComparator<T> {
    boolean isFuzzyEqual(T expected, T actual);
}
