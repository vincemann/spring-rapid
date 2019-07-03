package io.github.vincemann.generic.crud.lib.bidir;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.model.biDir.BiDirParentId;
import io.github.vincemann.generic.crud.lib.service.exception.UnknownParentTypeException;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a DTO Entity, which corresponding ServiceEntity, has a {@link io.github.vincemann.generic.crud.lib.model.biDir.BiDirParentEntity}.
 * The parent is represented in the DTO Entity by an id Field, annotated with {@link io.github.vincemann.generic.crud.lib.model.biDir.BiDirParentId}
 * @param <ParentId>    Type of ParentId
 */
public interface BiDirDTOChild<ParentId> {
    Map<Class,Field[]> biDirParentFieldsCache = new HashMap<>();

    default ParentId findParentId(Class<? extends IdentifiableEntity> parentClazz) throws UnknownParentTypeException, IllegalAccessException {
        Field[] parentIdFields = findParentIdFields();
        for (Field field: parentIdFields) {
            if(field.getAnnotation(BiDirParentId.class).value().equals(parentClazz)) {
                field.setAccessible(true);
                return (ParentId) field.get(this);
            }
        }
        throw new UnknownParentTypeException(this.getClass(),parentClazz);
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
