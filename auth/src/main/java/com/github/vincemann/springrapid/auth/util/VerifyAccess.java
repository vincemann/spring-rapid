package com.github.vincemann.springrapid.auth.util;

import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

public class VerifyAccess {

    public static <T> T notNull(T entity, String property) throws AccessDeniedException {
        if(entity == null){
            throw new AccessDeniedException("Property: "+property+" must not be null");
        }
        return entity;
    }

    public static void isTrue(boolean expression, String msg) throws AccessDeniedException {
        if(!expression){
            throw new AccessDeniedException(msg);
        }
    }

    public static <T> T isPresent(Optional<T> entity, String msg) throws AccessDeniedException {
        if (entity.isEmpty())
            throw new AccessDeniedException(msg);
        return entity.get();
    }

    public static <T> T isPresent(T entity, String msg) throws AccessDeniedException {
        if (entity == null)
            throw new AccessDeniedException(msg);
        return entity;
    }
}
