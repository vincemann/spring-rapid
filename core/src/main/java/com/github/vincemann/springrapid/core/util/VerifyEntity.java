package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class VerifyEntity {

    public static void notNull(String property, String propertyName) throws BadEntityException {
        if(property==null){
            throw new BadEntityException("Property: "+propertyName+" must not be null");
        }
    }

    public static void notEmpty(String property, String propertyName) throws BadEntityException {
        if(property==null){
            throw new BadEntityException("Property: "+propertyName+" must not be null");
        }
        if (property.isEmpty()){
            throw new BadEntityException("Property: "+propertyName+" must not be empty");
        }
    }

    public static void notEmpty(Collection<?> property, String propertyName) throws BadEntityException {
        if(property==null){
            throw new BadEntityException("Property: "+propertyName+" must not be null");
        }
        if (property.isEmpty()){
            throw new BadEntityException("Property: "+propertyName+" must not be empty");
        }
    }

    public static <T> T notNull(T entity, String property) throws BadEntityException {
        if(entity==null){
            throw new BadEntityException("Property: "+property+" must not be null");
        }
        return entity;
    }

    public static void isTrue(boolean expression, String msg) throws BadEntityException {
        if(!expression){
            throw new BadEntityException(msg);
        }
    }


    public static <T> T isPresent(T entity, String msg) throws EntityNotFoundException {
        if (entity==null){
            throw new EntityNotFoundException(msg);
        }
        return entity;
    }


    public static <T> T isPresent(Optional<T> entity, String msg) throws EntityNotFoundException {
        if (entity.isEmpty())
            throw new EntityNotFoundException(msg);
        return (T) entity.get();
    }

//    static <T> T isPresent(Object entity){
//        if(entity==null){
//            return null;
//        }
//        if (entity instanceof Optional){
//            if (((Optional) entity).isEmpty()){
//                return null;
//            }else {
//                return (T) ((Optional) entity).get();
//            }
//        }
//        return (T) entity;
//    }

    public static <T> T isPresent(T entity, Object id, Class clazz) throws EntityNotFoundException {
        if (entity==null){
            throw new EntityNotFoundException(id,clazz);
        }
        return entity;
    }

    public static <T> T isPresent(Optional<T> entity, Object id, Class clazz) throws EntityNotFoundException {
        if (entity.isEmpty()){
            throw new EntityNotFoundException(id,clazz);
        }
        return entity.get();
    }


}
