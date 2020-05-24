package io.github.vincemann.springrapid.core.util;

import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

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
        if(entity==null){
            throw new EntityNotFoundException(msg);
        }
        if (entity instanceof Optional){
            if (((Optional) entity).isEmpty()){
                throw new EntityNotFoundException(msg);
            }
        }
    }

    public static void checkPresent(Object entity, Object id, Class clazz) throws EntityNotFoundException {
        if(entity==null){
            throw new EntityNotFoundException(id,clazz);
        }
        if (entity instanceof Optional){
            if (((Optional) entity).isEmpty()){
                throw new EntityNotFoundException(id,clazz);
            }
        }
    }


}
