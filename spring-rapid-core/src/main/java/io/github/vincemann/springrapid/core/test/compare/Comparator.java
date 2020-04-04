package io.github.vincemann.springrapid.core.test.compare;

import java.util.List;
import java.util.Map;

public interface Comparator<T> {
    boolean isEqual(T expected, T actual);
    void reset();
    Map<String, List<Object>> getDiff();
}
