package io.github.vincemann.generic.crud.lib.test.compare;

import com.github.hervian.reflection.Types;
import io.github.vincemann.generic.crud.lib.util.MethodNameUtil;

import java.lang.reflect.Method;

public interface FullComparator<T> extends Comparator<T>{
    boolean isEqual(T expected, T actual);
    default void ignoreProperty(Types.Supplier<?> supplier){
        ignoreProperty(MethodNameUtil.propertyNameOf(supplier));
    }
    void ignoreProperty(String property);
    default void ignoreId(){
        ignoreProperty("id");
    }
}
