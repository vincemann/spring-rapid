package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.util.Optional;

public class RapidUtils {

    public static void checkNotNull(Object entity,String property) throws BadEntityException {
        if(entity==null){
            throw new BadEntityException("Property: " + property + " must not be null");
        }
    }

    public static void checkProperEntity(boolean expression, String msg) throws BadEntityException {
        if(!expression){
            throw new BadEntityException(msg);
        }
    }

    public static void checkPresent(Object entity, String msg) throws EntityNotFoundException {
        if (!checkPresent(entity)){
            throw new EntityNotFoundException(msg);
        }
    }

    public static boolean checkPresent(Object entity){
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

    public static void checkPresent(Object entity, Object id, Class clazz) throws EntityNotFoundException {
        if (!checkPresent(entity)){
            throw new EntityNotFoundException(id,clazz);
        }
    }


}
