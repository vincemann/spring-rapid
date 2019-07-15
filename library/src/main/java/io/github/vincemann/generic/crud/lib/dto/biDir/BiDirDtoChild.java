package io.github.vincemann.generic.crud.lib.dto.biDir;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParent;
import io.github.vincemann.generic.crud.lib.service.exception.UnknownParentTypeException;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a DTO Entity, that has n ParentEntities {@link BiDirDtoParent}
 * The parent is represented in the DTO Entity by an id Field, annotated with {@link BiDirParentId}
 *
 * This entity can be mapped to its ServiceEntity by using {@link io.github.vincemann.generic.crud.lib.controller.dtoMapper.IdResolvingDtoMapper}
 *
 */
public interface BiDirDtoChild {
    Map<Class,Field[]> biDirParentFieldsCache = new HashMap<>();

    default <ParentId extends Serializable & Comparable> ParentId findParentId(Class<? extends BiDirParent> parentClazz) throws UnknownParentTypeException, IllegalAccessException {
        Field[] parentIdFields = findParentIdFields();
        for (Field field: parentIdFields) {
            if(field.getAnnotation(BiDirParentId.class).value().equals(parentClazz)) {
                field.setAccessible(true);
                return (ParentId) field.get(this);
            }
        }
        throw new UnknownParentTypeException(this.getClass(),parentClazz);
    }

    default void addParentsId(BiDirParent biDirParent) throws IllegalAccessException {
        Serializable parentId = ((IdentifiableEntity) biDirParent).getId();
        if(parentId==null){
            throw new IllegalArgumentException("ParentId must not be null");
        }
        for(Field parentIdField: findParentIdFields()){
            if(parentIdField.getAnnotation(BiDirParentId.class).value().equals(biDirParent.getClass())){
                parentIdField.setAccessible(true);
                Object prevParent = parentIdField.get(this);
                if(prevParent!=null){
                    System.err.println("Warning, prev ParentId was not null -> overriding");
                }
                parentIdField.set(this,parentId);
            }
        }
    }

    default Map<Class,Serializable> findAllParentIds() throws IllegalAccessException {
        Map<Class,Serializable> parentIds = new HashMap<>();
        Field[] parentIdFields = findParentIdFields();
        for(Field field: parentIdFields){
            field.setAccessible(true);
            Serializable id = (Serializable) field.get(this);
            if(id!=null) {
                parentIds.put(field.getAnnotation(BiDirParentId.class).value(),id);
            }else {
                System.err.println("Warning: Null id found in BiDirDtoChild "+ this + " for ParentIdField with name: " + field.getName());
            }
        }
        return parentIds;
    }

    default Field[] findParentIdFields(){
        Field[] parentIdFieldsFromCache = biDirParentFieldsCache.get(this.getClass());
        if(parentIdFieldsFromCache==null){
            Field[] parentIdFields = ReflectionUtils.getDeclaredFieldsAnnotatedWith(getClass(), BiDirParentId.class, true);
            biDirParentFieldsCache.put(this.getClass(),parentIdFields);
            return parentIdFields;
        }else {
            return parentIdFieldsFromCache;
        }
    }
}
