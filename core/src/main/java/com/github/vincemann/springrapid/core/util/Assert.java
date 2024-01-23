package com.github.vincemann.springrapid.core.util;

import java.util.Optional;

public class Assert {

    public static <E extends Exception> void notNull(Object value, E exception) throws E {
        if (value == null)
            throw exception;
    }

    public static void notNull(Object value) {
        notNull(value,new IllegalArgumentException("Value must not be null"));
    }

    public static <E extends Exception> void isPresent(Optional<Object> optional, E exception) throws E{
        if (optional.isEmpty())
            throw exception;
    }

    public static void isPresent(Optional<Object> optional){
        isPresent(optional,new IllegalArgumentException("Value must be present"));
    }
}
