package io.github.vincemann.springrapid.coretest.compare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.springrapid.coretest.util.MethodNameUtil;

public interface FullComparator<T> extends Comparator<T>{
    boolean isEqual(T expected, T actual);
    default void ignoreProperty(Types.Supplier<?> supplier){
        ignoreProperty(MethodNameUtil.propertyNameOf(supplier));
    }
    void ignoreProperty(String property);
    default void ignoreId(){
        ignoreProperty("id");
    }
    void silentIgnore(boolean ignore);
    void ignoreNull(boolean ignore);
}
