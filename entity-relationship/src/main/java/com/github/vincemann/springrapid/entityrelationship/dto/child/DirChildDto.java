package com.github.vincemann.springrapid.entityrelationship.dto.child;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.parent.DirParent;
import com.github.vincemann.springrapid.entityrelationship.util.EntityIdAnnotationUtils;
import com.github.vincemann.springrapid.entityrelationship.util.EntityReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public interface DirChildDto {
    Logger log = LoggerFactory.getLogger(DirChildDto.class);

    default <ParentId extends Serializable> ParentId findParentId(Class<? extends DirParent> parentClazz, Class<? extends Annotation> parentIdAnnotationClass) throws UnknownParentTypeException {
        AtomicReference<ParentId> result = new AtomicReference<>();
        EntityReflectionUtils.doWithIdFieldsWithEntityType(parentClazz, parentIdAnnotationClass,getClass(), field -> {
            if (result.get()!=null){
                throw new IllegalArgumentException("There cant be two members with directional annotation type value");
            }
            result.set((ParentId) field.get(this));
        });
        if (result.get()==null){
            throw new UnknownChildTypeException(this.getClass(), parentClazz);
        }else {
            return result.get();
        }
    }

    default  <P extends DirParent> Map<Class<P>, Serializable> findAllParentIds(Class<? extends Annotation> parentIdAnnotationClass){
        final Map<Class<P>, Serializable> result = new HashMap<>();
        EntityReflectionUtils.doWithAnnotatedFields(parentIdAnnotationClass, getClass(), field -> {
            Serializable id = (Serializable) field.get(this);
            if (id != null) {
                result.put((Class<P>) EntityIdAnnotationUtils.getEntityType(field.getAnnotation(parentIdAnnotationClass)), id);
            } else {
                log.warn("Warning: Null id found in BiDirDtoChild " + this + " for ParentIdField with name: " + field.getName());
            }
        });
        return result;
    }

    default void addParentsId(DirParent parent,Class<? extends Annotation> parentIdAnnotationClass) {
        Serializable parentId = ((IdentifiableEntity) parent).getId();
        if (parentId == null) {
            throw new IllegalArgumentException("ParentId must not be null");
        }
        EntityReflectionUtils.doWithIdFieldsWithEntityType(parent.getClass(), parentIdAnnotationClass, getClass(), field -> {
            Object prevParentId = field.get(this);
            if (prevParentId != null) {
                log.warn("Warning, prev ParentId: " + prevParentId + " was not null -> overriding with new value: " + parentId);
            }
            field.set(this, parentId);
        });
    }
}
