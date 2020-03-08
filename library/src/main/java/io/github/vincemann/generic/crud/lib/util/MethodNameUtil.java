package io.github.vincemann.generic.crud.lib.util;

import com.github.hervian.reflection.Types;

public class MethodNameUtil {

    private MethodNameUtil(){}

    public static String propertyNameOf(Types.Supplier getter){
        return Types.createMethod(getter).getName();
    }
}
