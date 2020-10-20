package com.github.vincemann.springrapid.core.util;

import org.springframework.security.access.AccessDeniedException;

public class VerifyAccess {

    public static <T> T notNull(T entity, String property) throws AccessDeniedException {
        if(entity==null){
            throw new AccessDeniedException("Property: "+property+" of entity: "+entity+ " must not be null");
        }
        return entity;
    }

    public static void condition(boolean expression, String msg) throws AccessDeniedException {
        if(!expression){
            throw new AccessDeniedException(msg);
        }
    }

//    public static void isNot(boolean expression, String msg) throws AccessDeniedException {
//        if(expression){
//            throw new AccessDeniedException(msg);
//        }
//    }

    public static <T> T isPresent(Object entity, String msg) throws AccessDeniedException {
        Object present = VerifyEntity.isPresent(entity);
        if (present==null){
            throw new AccessDeniedException(msg);
        }
        return (T) entity;
    }

    public static <T> T isUserPresent(Object entity) throws AccessDeniedException {
        Object present = VerifyEntity.isPresent(entity);
        if (present==null){
            throw new AccessDeniedException("");
        }
        return (T) entity;
    }

//    public static <T> T isPresent(Object entity, Object id, Class clazz) throws AccessDeniedException {
//        Object present = VerifyEntity.isPresent(entity);
//        if (present==null){
//            throw new AccessDeniedException("Entity "id,clazz);
//        }
//        return (T) entity;
//    }
}
