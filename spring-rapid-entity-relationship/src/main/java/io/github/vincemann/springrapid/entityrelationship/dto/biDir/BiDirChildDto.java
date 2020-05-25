package io.github.vincemann.springrapid.entityrelationship.dto.biDir;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.config.ReflectionUtilsBean;
import io.github.vincemann.springrapid.entityrelationship.controller.dtomapper.IdResolvingDtoPostProcessor;
import io.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import io.github.vincemann.springrapid.entityrelationship.model.biDir.parent.BiDirParent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Dto, that has n Parent Entities.
 * Each parent is represented by an id Field, annotated with {@link BiDirParentId}.
 * <p>
 * This Dto can be mapped to its Entity by using {@link IdResolvingDtoPostProcessor}
 */
public interface BiDirChildDto {
    Logger log = LoggerFactory.getLogger(BiDirChildDto.class);

    default <ParentId extends Serializable> ParentId findBiDirParentId(Class<? extends BiDirParent> parentClazz) throws UnknownParentTypeException, IllegalAccessException {
        Field[] parentIdFields = findBiDirParentIdFields();
        for (Field field : parentIdFields) {
            if (field.getAnnotation(BiDirParentId.class).value().equals(parentClazz)) {
                field.setAccessible(true);
                return (ParentId) field.get(this);
            }
        }
        throw new UnknownParentTypeException(this.getClass(), parentClazz);
    }

    default void addBiDirParentsId(BiDirParent biDirParent) throws IllegalAccessException {
        Serializable parentId = ((IdentifiableEntity) biDirParent).getId();
        if (parentId == null) {
            throw new IllegalArgumentException("ParentId must not be null");
        }
        for (Field parentIdField : findBiDirParentIdFields()) {
            if (parentIdField.getAnnotation(BiDirParentId.class).value().equals(biDirParent.getClass())) {
                parentIdField.setAccessible(true);
                Object prevParentId = parentIdField.get(this);
                if (prevParentId != null) {
                    log.warn("Overriding previous parentId field. OldValue: " + prevParentId);
                }
                parentIdField.set(this, parentId);
            }
        }
    }

    default Map<Class, Serializable> findAllBiDirParentIds() throws IllegalAccessException {
        Map<Class, Serializable> parentIds = new HashMap<>();
        Field[] parentIdFields = findBiDirParentIdFields();
        for (Field field : parentIdFields) {
            field.setAccessible(true);
            Serializable id = (Serializable) field.get(this);
            if (id != null) {
                parentIds.put(field.getAnnotation(BiDirParentId.class).value(), id);
            } else {
                log.warn("Null ParentId found in BiDirDtoChild: " + this + " with idFieldName " + field.getName());
            }
        }
        return parentIds;
    }

    default Field[] findBiDirParentIdFields() {
        Field[] parentIdFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(getClass(), BiDirParentId.class);
        return parentIdFields;
    }
}
