package io.github.vincemann.generic.crud.lib.test.compare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.generic.crud.lib.util.MethodNameUtil;

public interface PropertyComparator {
    boolean isEqual(Object expected, Object actual);
    default void includeProperty(Types.Supplier<?> supplier){
        includeProperty(MethodNameUtil.propertyNameOf(supplier));
    }
    void includeProperty(String property);
    void reset();
}
