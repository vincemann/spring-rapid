package com.github.vincemann.springrapid.entityrelationship.dto.uniDir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.config.ReflectionUtilsBean;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.uniDir.parent.UniDirParent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * See {@link com.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirChildDto}
 */
public interface UniDirChildDto {
    Logger log = LoggerFactory.getLogger(UniDirChildDto.class);

    default <ParentId extends Serializable> ParentId findUniDirParentId(Class<? extends UniDirParent> parentClazz) throws UnknownParentTypeException, IllegalAccessException {
        AtomicReference<ParentId> result = new AtomicReference<>();
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            if (field.getAnnotation(UniDirParentId.class).value().equals(parentClazz)) {
                ReflectionUtils.makeAccessible(field);
                if (result.get()!=null){
                    throw new IllegalArgumentException("There cant be two members with directional annotation type value");
                }
                result.set((ParentId) field.get(this));
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(UniDirParentId.class));
        if (result.get()==null){
            throw new UnknownChildTypeException(this.getClass(), parentClazz);
        }else {
            return result.get();
        }

//        Field[] parentIdFields = findUniDirParentIdFields();
//        for (Field field : parentIdFields) {
//            if (field.getAnnotation(UniDirParentId.class).value().equals(parentClazz)) {
//                field.setAccessible(true);
//                return (ParentId) field.get(this);
//            }
//        }
//        throw new UnknownParentTypeException(this.getClass(), parentClazz);
    }

    default Map<Class, Serializable> findTypeUniDirParentIdMap() throws IllegalAccessException {
        final Map<Class, Serializable> result = new HashMap<>();
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            ReflectionUtils.makeAccessible(field);
            Serializable id = (Serializable) field.get(this);
            if (id != null) {
                result.put(field.getAnnotation(UniDirParentId.class).value(), id);
            } else {
                log.warn("Warning: Null id found in UniDirDtoChild " + this + " for ParentIdField with name: " + field.getName());
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(UniDirParentId.class));
        return result;


//        Map<Class, Serializable> parentIds = new HashMap<>();
//        Field[] parentIdFields = findUniDirParentIdFields();
//        for (Field field : parentIdFields) {
//            field.setAccessible(true);
//            Serializable id = (Serializable) field.get(this);
//            if (id != null) {
//                parentIds.put(field.getAnnotation(UniDirParentId.class).value(), id);
//            } else {
//                log.warn("Warning: Null id found in BiDirDtoChild " + this + " for ParentIdField with name: " + field.getName());
//            }
//        }
//        return parentIds;
    }

    default void addUniDirParentsId(IdentifiableEntity uniDirParent) throws IllegalAccessException {
        Serializable parentId = uniDirParent.getId();
        if (parentId == null) {
            throw new IllegalArgumentException("ParentId must not be null");
        }
        ReflectionUtils.doWithFields(this.getClass(),field -> {
                if (field.getAnnotation(UniDirParentId.class).value().equals(uniDirParent.getClass())) {
                    ReflectionUtils.makeAccessible(field);
                    Object prevParentId = field.get(this);
                    if (prevParentId != null) {
                        log.warn("Warning, prev ParentId: " + prevParentId + " was not null -> overriding with new value: " + parentId);
                    }
                    field.set(this, parentId);
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(UniDirParentId.class));
//        for (Field parentIdField : findUniDirParentIdFields()) {
//            if (parentIdField.getAnnotation(UniDirParentId.class).value().equals(uniDirParent.getClass())) {
//                ReflectionUtils.makeAccessible(parentIdField);
//                Object prevParentId = parentIdField.get(this);
//                if (prevParentId != null) {
//                    log.warn("Warning, prev ParentId: " + prevParentId + " was not null -> overriding with new value: " + parentId);
//                }
//                parentIdField.set(this, parentId);
//            }
//        }
    }

//    default Field[] findUniDirParentIdFields() {
//        Field[] parentIdFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(getClass(), UniDirParentId.class);
//        return parentIdFields;
//    }
}
