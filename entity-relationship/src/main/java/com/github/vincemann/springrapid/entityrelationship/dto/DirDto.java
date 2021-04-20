package com.github.vincemann.springrapid.entityrelationship.dto;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.model.DirEntity;
import com.github.vincemann.springrapid.entityrelationship.util.EntityIdAnnotationUtils;
import com.github.vincemann.springrapid.entityrelationship.util.EntityReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface DirDto {
    Logger log = LoggerFactory.getLogger(DirDto.class);


    default <C extends DirEntity> Map<Class<C>, Serializable> findEntityIds(Class<? extends Annotation> entityIdAnnotationType) {
        final Map<Class<C>, Serializable> result = new HashMap<>();
        EntityReflectionUtils.doWithAnnotatedFields(entityIdAnnotationType, getClass(), field -> {
            Serializable id = (Serializable) field.get(this);
            if (id != null) {
                result.put((Class<C>) EntityIdAnnotationUtils.getEntityType(field.getAnnotation(entityIdAnnotationType)), id);
            } else {
                log.warn("Null id found in BiDirParentDto " + this + " for EntityIdField with name: " + field.getName());
            }
        });
        return result;
    }

    default <C extends DirEntity> Map<Class<C>, Collection<Serializable>> findEntityIdCollections(Class<? extends Annotation> entityIdAnnotationType) {
        final Map<Class<C>, Collection<Serializable>> result = new HashMap<>();
        EntityReflectionUtils.doWithAnnotatedFields(entityIdAnnotationType,getClass(),field -> {
            Collection<Serializable> idCollection = (Collection<Serializable>) field.get(this);
            if (idCollection != null) {
                result.put((Class<C>) EntityIdAnnotationUtils.getEntityType(field.getAnnotation(entityIdAnnotationType)), idCollection);
            }/*else {
               throw new IllegalArgumentException("Null idCollection found in UniDirParentDto "+ this + " for ChildIdCollectionField with name: " + field.getName());
            }*/
        });
        return result;
    }

    default void addEntityId(DirEntity entity, Class<? extends Annotation> entityIdAnnotationClass, Class<? extends Annotation> entityIdCollectionAnnotationClass) {
        Serializable entityId = ((IdentifiableEntity) entity).getId();
        if (entityId == null) {
            throw new IllegalArgumentException("EntityId must not be null");
        }
        Map<Class<DirEntity>, Collection<Serializable>> entityIdCollections = findEntityIdCollections(entityIdCollectionAnnotationClass);
        //child collections
        for (Map.Entry<Class<DirEntity>, Collection<Serializable>> entityIdCollectionEntry : entityIdCollections.entrySet()) {
            if (entityIdCollectionEntry.getKey().equals(entity.getClass())) {
                //need to add
                Collection<Serializable> idCollection = entityIdCollectionEntry.getValue();
                //dirChild is always an Identifiable Child
                idCollection.add(entityId);
            }
        }

        EntityReflectionUtils.doWithIdFieldsWithEntityType(entity.getClass(), entityIdAnnotationClass, getClass(), field -> {
            Object prevEntityId = field.get(this);
            if (prevEntityId != null) {
                log.warn("Warning, prev EntityId: " + prevEntityId + " was not null -> overriding with new value: " + entityId);
            }
            field.set(this, entityId);
        });
    }
}
