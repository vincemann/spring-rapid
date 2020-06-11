package com.github.vincemann.springrapid.commons;

import java.lang.reflect.InvocationTargetException;

public class TestTypeInstances {

    public static <T> T create(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Could not create instance per default constructor",e);
        }
    }
}
