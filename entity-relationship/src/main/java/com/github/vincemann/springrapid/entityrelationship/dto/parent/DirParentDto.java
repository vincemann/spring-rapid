package com.github.vincemann.springrapid.entityrelationship.dto.parent;

import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.child.DirChild;
import com.github.vincemann.springrapid.entityrelationship.util.EntityIdAnnotationUtils;
import com.github.vincemann.springrapid.entityrelationship.util.EntityReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public interface DirParentDto {
    Logger log = LoggerFactory.getLogger(DirParentDto.class);


    default <ChildId extends Serializable> ChildId findChildId(Class<? extends DirChild> childClazz, Class<? extends Annotation> childIdAnnotationType) throws UnknownChildTypeException {
        AtomicReference<ChildId> result = new AtomicReference<>();
        EntityReflectionUtils.doWithIdFieldsWithEntityType(childClazz,childIdAnnotationType,getClass(),field -> {
            if (result.get() != null) {
                throw new IllegalArgumentException("There cant be two members with directional annotation type value");
            }
            result.set((ChildId) field.get(this));
        });
        if (result.get() == null) {
            throw new UnknownChildTypeException(this.getClass(), childClazz);
        } else {
            return result.get();
        }
    }

    default <C extends DirChild> Map<Class<C>, Serializable> findAllChildIds(Class<? extends Annotation> childIdAnnotationType) {
        final Map<Class<C>, Serializable> result = new HashMap<>();
        EntityReflectionUtils.doWithAnnotatedFields(childIdAnnotationType,getClass(),field -> {
            Serializable id = (Serializable) field.get(this);
            if (id != null) {
                result.put((Class<C>) EntityIdAnnotationUtils.getEntityType(field.getAnnotation(childIdAnnotationType)), id);
            } else {
                log.warn("Null id found in BiDirParentDto " + this + " for ChildIdField with name: " + field.getName());
            }
        });
        return result;
    }

    default <ChildId extends Serializable> Collection<ChildId> findChildIdCollection(Class<? extends DirChild> childClazz,Class<? extends Annotation> childIdAnnotationType)  {
        AtomicReference<Collection<ChildId>> result = new AtomicReference<>();
        EntityReflectionUtils.doWithAnnotatedFields(childIdAnnotationType,getClass(),field -> {
            if (result.get() != null) {
                throw new IllegalArgumentException("There cant be two members with directional annotation type value");
            }
            result.set((Collection<ChildId>) field.get(this));
        });
        if (result.get() == null) {
            throw new UnknownChildTypeException(this.getClass(), childClazz);
        } else {
            return result.get();
        }
    }

    default <C extends DirChild> Map<Class<C>, Collection<Serializable>> findAllChildIdCollections(Class<? extends Annotation> childIdAnnotationType) {
        final Map<Class<C>, Collection<Serializable>> result = new HashMap<>();
        EntityReflectionUtils.doWithAnnotatedFields(childIdAnnotationType,getClass(),field -> {
            Collection<Serializable> idCollection = (Collection<Serializable>) field.get(this);
            if (idCollection != null) {
                result.put((Class<C>) EntityIdAnnotationUtils.getEntityType(field.getAnnotation(childIdAnnotationType)), idCollection);
            }/*else {
               throw new IllegalArgumentException("Null idCollection found in UniDirParentDto "+ this + " for ChildIdCollectionField with name: " + field.getName());
            }*/
        });
        return result;
    }

}
