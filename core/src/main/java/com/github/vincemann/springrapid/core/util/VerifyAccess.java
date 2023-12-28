package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

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

//    public static <T> T isUserPresent(T entity) throws AccessDeniedException {
//        Object present = VerifyEntity.isPresent(entity);
//        if (present==null){
//            throw new AccessDeniedException("");
//        }
//        return (T) entity;
//    }

//    public static <T> T isPresent(Object entity, Object id, Class clazz) throws AccessDeniedException {
//        Object present = VerifyEntity.isPresent(entity);
//        if (present==null){
//            throw new AccessDeniedException("Entity "id,clazz);
//        }
//        return (T) entity;
//    }
}
