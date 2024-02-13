package com.github.vincemann.springrapid.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeResolver {
    /**
     * Searches through the class hierarchy to find a parameterized type and returns the first generic parameter.
     * @param clazz The class to start the search from.
     * @return The first generic type parameter of the first parameterized superclass/interface, or null if not found.
     */
    public static Class<?> findFirstGenericParameter(Class<?> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();

        // Traverse the class hierarchy
        while (clazz != null && clazz != Object.class) {
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > 0) {
                    if (typeArguments[0] instanceof Class) {
                        return (Class<?>) typeArguments[0];
                    } else {
                        // The first generic parameter is not a direct class (could be a generic array, wildcard, etc.)
                        return null;
                    }
                }
            }
            // Move up the hierarchy
            clazz = clazz.getSuperclass();
            genericSuperclass = clazz.getGenericSuperclass();
        }

        return null; // No parameterized superclass/interface with a generic parameter was found.
    }

}
