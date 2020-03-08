package io.github.vincemann.generic.crud.lib.test.compare;

import com.github.hervian.reflection.Types;

import java.lang.reflect.Method;

public interface ReflectionComparator<T> {

    boolean isEqual(T expected, T actual);

    default void ignoreProperty(Types.Supplier<?> supplier){
        Method getterMethod = Types.createMethod(supplier);
        String getterName = getterMethod.getName();
        String propertyName = "";
        if (getterName.startsWith("get")){
            propertyName = getterName.replace("get","");
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
        }else if(getterName.startsWith("is")){
            propertyName = getterName.replace("is","");
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
        }else {
            throw new IllegalArgumentException("Not a getterName: " + supplier.toString());
        }
        ignoreProperty(propertyName);
    }


    void ignoreProperty(String property);
}
