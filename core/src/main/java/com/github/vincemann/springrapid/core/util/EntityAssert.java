package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.util.Optional;

public class EntityAssert {

    public static void notNull(Object entity, String property) throws BadEntityException {
        if(entity==null){
            throw new BadEntityException("Property: " + property + " must not be null");
        }
    }

    public static void isTrue(boolean expression, String msg) throws BadEntityException {
        if(!expression){
            throw new BadEntityException(msg);
        }
    }

    public static void isPresent(Object entity, String msg) throws EntityNotFoundException {
        if (!isPresent(entity)){
            throw new EntityNotFoundException(msg);
        }
    }

    public static boolean isPresent(Object entity){
        if(entity==null){
            return false;
        }
        if (entity instanceof Optional){
            if (((Optional) entity).isEmpty()){
                return false;
            }
        }
        return true;
    }

    public static void isPresent(Object entity, Object id, Class clazz) throws EntityNotFoundException {
        if (!isPresent(entity)){
            throw new EntityNotFoundException(id,clazz);
        }
    }


}
