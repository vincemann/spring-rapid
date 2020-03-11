package io.github.vincemann.generic.crud.lib.util;

import com.github.hervian.reflection.Types;

import java.lang.reflect.Method;

public class MethodNameUtil {

    private MethodNameUtil(){}

    public static String propertyNameOf(Types.Supplier getter){
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


}
