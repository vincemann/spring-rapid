package com.github.vincemann.springrapid.entityrelationship.dto.uniDir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.config.ReflectionUtilsBean;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.uniDir.parent.UniDirParent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * See {@link com.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirChildDto}
 */
public interface UniDirChildDto {
    Logger log = LoggerFactory.getLogger(UniDirChildDto.class);

    default <ParentId extends Serializable> ParentId findUniDirParentId(Class<? extends UniDirParent> parentClazz) throws UnknownParentTypeException, IllegalAccessException {
        Field[] parentIdFields = findUniDirParentIdFields();
        for (Field field : parentIdFields) {
            if (field.getAnnotation(UniDirParentId.class).value().equals(parentClazz)) {
                field.setAccessible(true);
                return (ParentId) field.get(this);
            }
        }
        throw new UnknownParentTypeException(this.getClass(), parentClazz);
    }

    default Map<Class, Serializable> findAllUniDirParentIds() throws IllegalAccessException {
        Map<Class, Serializable> parentIds = new HashMap<>();
        Field[] parentIdFields = findUniDirParentIdFields();
        for (Field field : parentIdFields) {
            field.setAccessible(true);
            Serializable id = (Serializable) field.get(this);
            if (id != null) {
                parentIds.put(field.getAnnotation(UniDirParentId.class).value(), id);
            } else {
                log.warn("Warning: Null id found in BiDirDtoChild " + this + " for ParentIdField with name: " + field.getName());
            }
        }
        return parentIds;
    }

    default void addUniDirParentsId(IdentifiableEntity uniDirParent) throws IllegalAccessException {
        Serializable parentId = uniDirParent.getId();
        if (parentId == null) {
            throw new IllegalArgumentException("ParentId must not be null");
        }
        for (Field parentIdField : findUniDirParentIdFields()) {
            if (parentIdField.getAnnotation(UniDirParentId.class).value().equals(uniDirParent.getClass())) {
                parentIdField.setAccessible(true);
                Object prevParentId = parentIdField.get(this);
                if (prevParentId != null) {
                    log.warn("Warning, prev ParentId: " + prevParentId + " was not null -> overriding with new value: " + parentId);
                }
                parentIdField.set(this, parentId);
            }
        }
    }

    default Field[] findUniDirParentIdFields() {
        Field[] parentIdFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(getClass(), UniDirParentId.class);
        return parentIdFields;
    }
}
