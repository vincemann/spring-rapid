package io.github.vincemann.generic.crud.lib.dto.uniDir;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParent;
import io.github.vincemann.generic.crud.lib.model.uniDir.UniDirParent;
import io.github.vincemann.generic.crud.lib.service.exception.UnknownParentTypeException;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public interface UniDirChildDto {
    Map<Class, Field[]> uniDirParentFieldsCache = new HashMap<>();

    default <ParentId extends Serializable> ParentId findParentId(Class<? extends UniDirParent> parentClazz) throws UnknownParentTypeException, IllegalAccessException {
        Field[] parentIdFields = findParentIdFields();
        for (Field field: parentIdFields) {
            if(field.getAnnotation(UniDirParentId.class).value().equals(parentClazz)) {
                field.setAccessible(true);
                return (ParentId) field.get(this);
            }
        }
        throw new UnknownParentTypeException(this.getClass(),parentClazz);
    }

    default Map<Class,Serializable> findAllParentIds() throws IllegalAccessException {
        Map<Class,Serializable> parentIds = new HashMap<>();
        Field[] parentIdFields = findParentIdFields();
        for(Field field: parentIdFields){
            field.setAccessible(true);
            Serializable id = (Serializable) field.get(this);
            if(id!=null) {
                parentIds.put(field.getAnnotation(UniDirParentId.class).value(),id);
            }else {
                System.err.println("Warning: Null id found in BiDirDtoChild "+ this + " for ParentIdField with name: " + field.getName());
            }
        }
        return parentIds;
    }

    default void addParentsId(IdentifiableEntity uniDirParent) throws IllegalAccessException {
        Serializable parentId =  uniDirParent.getId();
        if(parentId==null){
            throw new IllegalArgumentException("ParentId must not be null");
        }
        for(Field parentIdField: findParentIdFields()){
            if(parentIdField.getAnnotation(UniDirParentId.class).value().equals(uniDirParent.getClass())){
                parentIdField.setAccessible(true);
                Object prevParent = parentIdField.get(this);
                if(prevParent!=null){
                    System.err.println("Warning, prev ParentId was not null -> overriding");
                }
                parentIdField.set(this,parentId);
            }
        }
    }

    default Field[] findParentIdFields(){
        Field[] parentIdFieldsFromCache = uniDirParentFieldsCache.get(this.getClass());
        if(parentIdFieldsFromCache==null){
            Field[] parentIdFields = ReflectionUtils.getDeclaredFieldsAnnotatedWith(getClass(), UniDirParentId.class, true);
            uniDirParentFieldsCache.put(this.getClass(),parentIdFields);
            return parentIdFields;
        }else {
            return parentIdFieldsFromCache;
        }
    }
}
