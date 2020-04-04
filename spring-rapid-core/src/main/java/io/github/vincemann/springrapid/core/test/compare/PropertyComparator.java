package io.github.vincemann.springrapid.core.test.compare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.springrapid.core.util.MethodNameUtil;


public interface PropertyComparator extends Comparator<Object>{

    default void includeProperty(Types.Supplier<?> supplier){
        includeProperty(MethodNameUtil.propertyNameOf(supplier));
    }
    void includeProperty(String property);
}
