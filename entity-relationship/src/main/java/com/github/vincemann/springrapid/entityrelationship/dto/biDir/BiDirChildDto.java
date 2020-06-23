package com.github.vincemann.springrapid.entityrelationship.dto.biDir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.config.ReflectionUtilsBean;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.IdResolvingDtoPostProcessor;
import com.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirParentId;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.biDir.parent.BiDirParent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a Dto, that has n Parent Entities.
 * Each parent is represented by an id Field, annotated with {@link BiDirParentId}.
 * <p>
 * This Dto can be mapped to its Entity by using {@link IdResolvingDtoPostProcessor}
 */
public interface BiDirChildDto {
    Logger log = LoggerFactory.getLogger(BiDirChildDto.class);

    default <ParentId extends Serializable> ParentId findBiDirParentId(Class<? extends BiDirParent> parentClazz) throws UnknownParentTypeException, IllegalAccessException {
        AtomicReference<ParentId> result = new AtomicReference<>();
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            if (field.getAnnotation(BiDirParentId.class).value().equals(parentClazz)) {
                ReflectionUtils.makeAccessible(field);
                if (result.get()!=null){
                    throw new IllegalArgumentException("There cant be two members with directional annotation type value");
                }
                result.set((ParentId) field.get(this));
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(BiDirParentId.class));
        if (result.get()==null){
            throw new UnknownChildTypeException(this.getClass(), parentClazz);
        }else {
            return result.get();
        }


//        Field[] parentIdFields = findBiDirParentIdFields();
//        for (Field field : parentIdFields) {
//            if (field.getAnnotation(BiDirParentId.class).value().equals(parentClazz)) {
//                field.setAccessible(true);
//                return (ParentId) field.get(this);
//            }
//        }
//        throw new UnknownParentTypeException(this.getClass(), parentClazz);
    }

    default void addBiDirParentsId(BiDirParent biDirParent) throws IllegalAccessException {
        Serializable parentId = ((IdentifiableEntity) biDirParent).getId();
        if (parentId == null) {
            throw new IllegalArgumentException("ParentId must not be null");
        }
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            if (field.getAnnotation(BiDirParentId.class).value().equals(biDirParent.getClass())) {
                ReflectionUtils.makeAccessible(field);
                Object prevParentId = field.get(this);
                if (prevParentId != null) {
                    log.warn("Warning, prev ParentId: " + prevParentId + " was not null -> overriding with new value: " + parentId);
                }
                field.set(this, parentId);
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(BiDirParentId.class));

//        Serializable parentId = ((IdentifiableEntity) biDirParent).getId();
//        if (parentId == null) {
//            throw new IllegalArgumentException("ParentId must not be null");
//        }
//        for (Field parentIdField : findBiDirParentIdFields()) {
//            if (parentIdField.getAnnotation(BiDirParentId.class).value().equals(biDirParent.getClass())) {
//                parentIdField.setAccessible(true);
//                Object prevParentId = parentIdField.get(this);
//                if (prevParentId != null) {
//                    log.warn("Overriding previous parentId field. OldValue: " + prevParentId);
//                }
//                parentIdField.set(this, parentId);
//            }
//        }
    }

    default Map<Class, Serializable> findTypeBiDirParentIdMap() throws IllegalAccessException {
        final Map<Class, Serializable> result = new HashMap<>();
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            ReflectionUtils.makeAccessible(field);
            Serializable id = (Serializable) field.get(this);
            if (id != null) {
                result.put(field.getAnnotation(BiDirParentId.class).value(), id);
            } else {
                log.warn("Warning: Null id found in BiDirDtoChild " + this + " for ParentIdField with name: " + field.getName());
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(BiDirParentId.class));
        return result;

//        Map<Class, Serializable> parentIds = new HashMap<>();
//        Field[] parentIdFields = findBiDirParentIdFields();
//        for (Field field : parentIdFields) {
//            field.setAccessible(true);
//            Serializable id = (Serializable) field.get(this);
//            if (id != null) {
//                parentIds.put(field.getAnnotation(BiDirParentId.class).value(), id);
//            } else {
//                log.warn("Null ParentId found in BiDirDtoChild: " + this + " with idFieldName " + field.getName());
//            }
//        }
//        return parentIds;
    }

//    default Field[] findBiDirParentIdFields() {
//        Field[] parentIdFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(getClass(), BiDirParentId.class);
//        return parentIdFields;
//    }
}
