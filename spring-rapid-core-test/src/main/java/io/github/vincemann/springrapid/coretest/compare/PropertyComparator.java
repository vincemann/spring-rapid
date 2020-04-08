package io.github.vincemann.springrapid.coretest.compare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.springrapid.coretest.util.MethodNameUtil;


public interface PropertyComparator extends Comparator<Object>{

    default void includeProperty(Types.Supplier<?> supplier){
        includeProperty(MethodNameUtil.propertyNameOf(supplier));
    }
    void includeProperty(String property);
}
