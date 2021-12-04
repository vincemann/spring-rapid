package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.util.Optional;

public class VerifyEntity {

    public static void notNull(String property, String propertyName) throws BadEntityException {
        if(property==null){
            throw new BadEntityException("Property: "+propertyName+" must not be null");
        }
    }

    public static <T> T notNull(T entity, String property) throws BadEntityException {
        if(entity==null){
            throw new BadEntityException("Property: "+property+" must not be null");
        }
        return entity;
    }

    public static void is(boolean expression, String msg) throws BadEntityException {
        if(!expression){
            throw new BadEntityException(msg);
        }
    }

    public static void isNot(boolean expression, String msg) throws BadEntityException {
        if(expression){
            throw new BadEntityException(msg);
        }
    }

    public static <T> T isPresent(Object entity, String msg) throws EntityNotFoundException {
        Object present = isPresent(entity);
        if (present==null){
            throw new EntityNotFoundException(msg);
        }
        return (T) entity;
    }

    static <T> T isPresent(Object entity){
        if(entity==null){
            return null;
        }
        if (entity instanceof Optional){
            if (((Optional) entity).isEmpty()){
                return null;
            }else {
                return (T) ((Optional) entity).get();
            }
        }
        return (T) entity;
    }

    public static <T> T isPresent(Object entity, Object id, Class clazz) throws EntityNotFoundException {
        Object present = isPresent(entity);
        if (present==null){
            throw new EntityNotFoundException(id,clazz);
        }
        return (T) entity;
    }


}
