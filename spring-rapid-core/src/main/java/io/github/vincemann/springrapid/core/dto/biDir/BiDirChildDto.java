package io.github.vincemann.springrapid.core.dto.biDir;

import io.github.vincemann.springrapid.core.controller.dtoMapper.idResolver.IdResolvingDtoMapper;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.model.biDir.parent.BiDirParent;
import io.github.vincemann.springrapid.core.service.exception.entityRelationHandling.UnknownParentTypeException;
import io.github.vincemann.springrapid.core.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Dto Entity, that has n ParentEntities {@link BiDirParentDto}
 * The parent is represented in the Dto Entity by an id Field, annotated with {@link BiDirParentId}
 *
 * This entity can be mapped to its ServiceEntity by using {@link IdResolvingDtoMapper}
 *
 */
public interface BiDirChildDto {
    Logger log = LoggerFactory.getLogger(BiDirChildDto.class);
    Map<Class,Field[]> biDirParentFieldsCache = new HashMap<>();

    default <ParentId extends Serializable> ParentId findBiDirParentId(Class<? extends BiDirParent> parentClazz) throws UnknownParentTypeException, IllegalAccessException {
        Field[] parentIdFields = findBiDirParentIdFields();
        for (Field field: parentIdFields) {
            if(field.getAnnotation(BiDirParentId.class).value().equals(parentClazz)) {
                field.setAccessible(true);
                return (ParentId) field.get(this);
            }
        }
        throw new UnknownParentTypeException(this.getClass(),parentClazz);
    }

    default void addBiDirParentsId(BiDirParent biDirParent) throws IllegalAccessException {
        Serializable parentId = ((IdentifiableEntity) biDirParent).getId();
        if(parentId==null){
            throw new IllegalArgumentException("ParentId must not be null");
        }
        for(Field parentIdField: findBiDirParentIdFields()){
            if(parentIdField.getAnnotation(BiDirParentId.class).value().equals(biDirParent.getClass())){
                parentIdField.setAccessible(true);
                Object prevParentId = parentIdField.get(this);
                if(prevParentId!=null){
                    log.warn("Overriding previous parentId field. OldValue: "+ prevParentId);
                }
                parentIdField.set(this,parentId);
            }
        }
    }

    default Map<Class,Serializable> findAllBiDirParentIds() throws IllegalAccessException {
        Map<Class,Serializable> parentIds = new HashMap<>();
        Field[] parentIdFields = findBiDirParentIdFields();
        for(Field field: parentIdFields){
            field.setAccessible(true);
            Serializable id = (Serializable) field.get(this);
            if(id!=null) {
                parentIds.put(field.getAnnotation(BiDirParentId.class).value(),id);
            }else {
                log.warn("Null ParentId found in BiDirDtoChild: "+this+" with idFieldName "+field.getName());
            }
        }
        return parentIds;
    }

    default Field[] findBiDirParentIdFields(){
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
