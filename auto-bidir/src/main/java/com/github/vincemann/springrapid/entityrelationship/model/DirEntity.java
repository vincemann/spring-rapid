package com.github.vincemann.springrapid.entityrelationship.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownEntityTypeException;
import com.github.vincemann.springrapid.entityrelationship.util.CollectionUtils;
import com.github.vincemann.springrapid.entityrelationship.util.EntityAnnotationUtils;
import com.github.vincemann.springrapid.entityrelationship.util.EntityReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


public interface DirEntity {
    Logger log = LoggerFactory.getLogger(DirEntity.class);

    default <C> Set<C> findSingleEntities(Class<? extends Annotation> annotationClass){
        Set<C> entities = new HashSet<>();
        EntityReflectionUtils.doWithAnnotatedFields(annotationClass,getClass(), field -> {
            C entity = (C) field.get(this);
            if(entity == null){
                //skip
                return;
            }
            entities.add(entity);
        });
        return entities;
    }

    /**
     * Find the Entity Collections (all fields of this annotated with @param entityAnnotationClass)
     * mapped to the Type of the Entities in the Collection.
     *
     * @return
     */
    default <C> Map<Collection<C>, Class<C>> findEntityCollections(Class<? extends Annotation> entityAnnotationClass) {
        Map<Collection<C>, Class<C>> entityCollection_entityTypeMap = new HashMap<>();
        EntityReflectionUtils.doWithAnnotatedFields(entityAnnotationClass, getClass(), field -> {
            Collection<C> entityCollection = (Collection<C>) field.get(this);
            if (entityCollection == null) {
                //throw new IllegalArgumentException("Null idCollection found in BiDirParent "+ this + " for EntityCollectionField with name: " + field.getName());
                log.warn("Auto-generating Collection for null valued BiDirEntityCollection Field: " + field);
                Collection emptyCollection = CollectionUtils.createEmptyCollection(field);
                field.set(this, emptyCollection);
                entityCollection = emptyCollection;
            }
            Class<C> entityType = (Class<C>) EntityAnnotationUtils.getEntityType(field.getAnnotation(entityAnnotationClass));
            entityCollection_entityTypeMap.put(entityCollection, entityType);
        });
        return entityCollection_entityTypeMap;
    }

    default void linkEntity(IdentifiableEntity newEntity, Class<? extends Annotation> entityAnnotationClass, Class<? extends Annotation> entityCollectionAnnotationClass) throws UnknownEntityTypeException {
        AtomicBoolean added = new AtomicBoolean(false);
        //add to matching entity collections
        for (Map.Entry<Collection<IdentifiableEntity>, Class<IdentifiableEntity>> entry : this.<IdentifiableEntity>findEntityCollections(entityCollectionAnnotationClass).entrySet()) {
            Class<? extends IdentifiableEntity> targetClass = entry.getValue();
            if (newEntity.getClass().equals(targetClass)) {
                (entry.getKey()).add(newEntity);
                added.set(true);
            }
        }
        //set matching entity
        EntityReflectionUtils.doWithAnnotatedFieldsOfType(newEntity.getClass(),entityAnnotationClass,getClass(),entityField -> {
            IdentifiableEntity oldEntity = (IdentifiableEntity) entityField.get(this);
            if (oldEntity != null) {
                log.warn("Overriding old entity: " + oldEntity + " with new entity " + newEntity + " of source sntity " + this);
            }
            entityField.set(this, newEntity);
            added.set(true);
        });
        if (!added.get()) {
            throw new UnknownEntityTypeException(getClass(), newEntity.getClass());
        }
    }

    default void unlinkEntity(IdentifiableEntity entityToRemove, Class<? extends Annotation> entityEntityAnnotationClass, Class<? extends Annotation> entityEntityCollectionAnnotationClass) throws UnknownEntityTypeException{
        AtomicBoolean deleted = new AtomicBoolean(false);
        for (Map.Entry<Collection<IdentifiableEntity>, Class<IdentifiableEntity>> entry : this.<IdentifiableEntity>findEntityCollections(entityEntityCollectionAnnotationClass).entrySet()) {
            Collection<IdentifiableEntity> entityCollection = entry.getKey();
            if(entityCollection!=null){
                if(!entityCollection.isEmpty()){
                    Optional<IdentifiableEntity> optionalEntity = entityCollection.stream().findFirst();
                    if(optionalEntity.isPresent()){
                        IdentifiableEntity entity = optionalEntity.get();
                        if(entityToRemove.getClass().equals(entity.getClass())){
                            //this set needs to remove the entity
                            //here is a hibernate bug in persistent set remove function, see https://stackoverflow.com/a/47968974
                            //therefor we use an odd workaround
                            Iterator<IdentifiableEntity> iterator = entityCollection.iterator();
                            while (iterator.hasNext()){
                                IdentifiableEntity e = iterator.next();
                                if (e.equals(entityToRemove)){
                                    iterator.remove();
                                    deleted.set(true);
                                    break;
                                }
                            }
                            // old workaround, which does not work for n-m remove
//                            List<DirEntity> toRemove = new ArrayList<>();
//                            toRemove.add(entityToRemove);
//                            boolean successfulRemove = entityCollection.removeAll(toRemove);
//                            //entityCollection = PersistentSet
//                            if(!successfulRemove){
//                                throw new RuntimeException("Entity: "+toRemove+", which should be deleted from source entity-collection, was not present in it or delete operation was not successful. "+this);
//                            }else {
//                                deleted.set(true);
//                            }
                        }
                    }
                }
            }
        }
        EntityReflectionUtils.doWithAnnotatedFields(entityEntityAnnotationClass,getClass(),entityField -> {
            IdentifiableEntity entity = (IdentifiableEntity) entityField.get(this);
            if(entity!=null) {
                if (entity.getClass().equals(entityToRemove.getClass())) {
                    entityField.set(this, null);
                    deleted.set(true);
                }
            }
        });
        if(!deleted.get()){
            System.err.println("it is also possible, that entity to unlink was not found in collection");
            throw new UnknownEntityTypeException(this.getClass(), entityToRemove.getClass());
        }
    }
}
