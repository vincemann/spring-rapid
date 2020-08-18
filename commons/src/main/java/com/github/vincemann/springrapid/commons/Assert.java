package com.github.vincemann.springrapid.commons;

import java.util.Optional;

public class Assert {

    public static <T> T notNull(T entity) {
        if(entity==null){
            throw new NullPointerException("Property must not be null");
        }
        return entity;
    }

    public static <T> T notNull(T entity, String property) {
        if(entity==null){
            throw new NullPointerException("Property: " + property + " must not be null");
        }
        return entity;
    }

    public static <T> T isPresent(Object o){
        if(o==null){
            throw new NullPointerException("Object must not be null");
        }
        if (o instanceof Optional){
            if (((Optional<T>) o).isEmpty()){
                throw new IllegalArgumentException("Object must be present");
            }else {
                return ((Optional<T>) o).get();
            }
        }
        return (T) o;
    }

    public static <T> T isPresent(Object o, String argName){
        if(o==null){
            throw new NullPointerException("Object:"+argName+" must not be null");
        }
        if (o instanceof Optional){
            if (((Optional<T>) o).isEmpty()){
                throw new IllegalArgumentException("Object:"+argName+" must be present");
            }else {
                return ((Optional<T>) o).get();
            }
        }
        return (T) o;
    }
}
