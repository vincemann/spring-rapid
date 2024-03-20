package com.github.vincemann.springrapid.core.util;

import com.github.hervian.reflection.Types;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodNameUtil {

    private MethodNameUtil(){}

    public static String propertyName(Types.Supplier getter){
        Method getterMethod = Types.createMethod(getter);
        String getterName = getterMethod.getName();
        String propertyName = "";
        if (getterName.startsWith("get")){
            propertyName = getterName.replace("get","");
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
        }else if(getterName.startsWith("is")){
            propertyName = getterName.replace("is","");
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
        }else {
            throw new IllegalArgumentException("Not a getterName: " + getter.toString());
        }
        return propertyName;
    }

    public static String[] propertyNamesOf(Types.Supplier<?>... getters) {
        return Arrays.stream(getters)
                .map(MethodNameUtil::propertyName)
                .distinct()
                .toArray(String[]::new);
    }


}
