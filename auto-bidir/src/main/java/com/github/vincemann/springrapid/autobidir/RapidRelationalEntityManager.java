package com.github.vincemann.springrapid.autobidir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.autobidir.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.autobidir.exception.UnknownEntityTypeException;
import com.github.vincemann.springrapid.autobidir.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.autobidir.model.RelationalEntityType;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildEntity;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.UniDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.UniDirChildEntity;
import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentCollection;
import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentEntity;
import com.github.vincemann.springrapid.autobidir.util.CollectionUtils;
import com.github.vincemann.springrapid.autobidir.util.EntityAnnotationUtils;
import com.github.vincemann.springrapid.autobidir.util.EntityReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class RapidRelationalEntityManager implements RelationalEntityManager{


    @Cacheable(value = "entityRelationTypes")
    @Override
    public Set<RelationalEntityType> inferTypes(Class<? extends IdentifiableEntity> entityClass) {
        Set<RelationalEntityType> relationalEntityTypes = new HashSet<>();
        org.springframework.util.ReflectionUtils.doWithFields(entityClass, field -> {
            org.springframework.util.ReflectionUtils.makeAccessible(field);
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {

                if (annotation.annotationType().equals(BiDirChildEntity.class)){
                    relationalEntityTypes.add(RelationalEntityType.BiDirParent);
                }
                if (annotation.annotationType().equals(BiDirChildCollection.class)){
                    relationalEntityTypes.add(RelationalEntityType.BiDirParent);
                }


                if (annotation.annotationType().equals(UniDirChildEntity.class)){
                    relationalEntityTypes.add(RelationalEntityType.UniDirParent);
                }
                if (annotation.annotationType().equals(UniDirChildCollection.class)){
                    relationalEntityTypes.add(RelationalEntityType.UniDirParent);
                }


                if (annotation.annotationType().equals(BiDirParentEntity.class)){
                    relationalEntityTypes.add(RelationalEntityType.BiDirChild);
                }
                if (annotation.annotationType().equals(BiDirParentCollection.class)){
                    relationalEntityTypes.add(RelationalEntityType.BiDirChild);
                }
            }

            // dont apply filter bc we also need to check for sets of IdentifiableEntities
        }/*, field -> IdentifiableEntity.class.isAssignableFrom(field.getType())*/);
        return relationalEntityTypes;
    }


    // BiDirChild methods



    /**
     * Find the BiDirParent Collections (all fields of this parent annotated with {@link BiDirParentCollection} )
     * mapped to the Type of the Entities in the Collection.
     * @return
     */
    public Map<Collection<IdentifiableEntity>,Class<IdentifiableEntity>> findBiDirParentCollections(IdentifiableEntity child){
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        return findEntityCollections(child,BiDirParentCollection.class);
    }
    /**
     *
     * @return  all parent of this, that are not null
     */
    public Collection<IdentifiableEntity> findSingleBiDirParents(IdentifiableEntity child) {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        return findSingleEntities(child,BiDirParentEntity.class);
    }

    /**
     *
     * @param parentToSet
     * @throws UnknownParentTypeException   when supplied Parent does not match any of the fields in child class anntoated with {@link BiDirParentEntity}
     */
    public void linkBiDirParent(IdentifiableEntity child, IdentifiableEntity parentToSet) throws UnknownParentTypeException {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        assertEntityRelationType(parentToSet, RelationalEntityType.BiDirParent);
        linkEntity(child, parentToSet,BiDirParentEntity.class,BiDirParentCollection.class);
    }

    public void unlinkBiDirParents(IdentifiableEntity child) throws UnknownChildTypeException, UnknownParentTypeException{
        for(IdentifiableEntity parent: findSingleBiDirParents(child)){
            if(parent!=null) {
                unlinkBiDirParent(child, parent);
            }else {
                log.warn("Parent Reference of BiDirChild with type: "+child.getClass().getSimpleName()+" was not set when deleting -> parent was deleted before child");
            }
        }
    }

    /**
     * This Child wont know about parentToDelete after this operation.
     * Set all {@link BiDirParentEntity}s of this BiDirChild to null.
     * @param parentToDelete
     * @throws UnknownParentTypeException   thrown, if parentToDelete is of unknown type -> no field , annotated as {@link BiDirParentEntity}, with the most specific type of parentToDelete, exists in Child (this).
     */
    public void unlinkBiDirParent(IdentifiableEntity child, IdentifiableEntity parentToDelete) throws UnknownParentTypeException {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        assertEntityRelationType(parentToDelete, RelationalEntityType.BiDirParent);
        unlinkEntity(child, parentToDelete,BiDirParentEntity.class,BiDirParentCollection.class);
    }

    /**
     * All children BiDirChild of this parent wont know about this parent, after this operation.
     * Clear all {@link BiDirChildCollection}s of this parent.
     * Call this, before you want to delete this parent.
     * @throws UnknownParentTypeException
     */
    public void unlinkParentsChildren(IdentifiableEntity child) throws UnknownEntityTypeException {
        for(IdentifiableEntity parent: findSingleBiDirParents(child)){
            unlinkBiDirChild(parent,child);
        }
        for(Map.Entry<Collection<IdentifiableEntity>,Class<IdentifiableEntity>> entry: findBiDirParentCollections(child).entrySet()){
            Collection<IdentifiableEntity> parentCollection = entry.getKey();
            for(IdentifiableEntity parent: parentCollection){
                unlinkBiDirChild(parent,child);
            }
            parentCollection.clear();
        }
    }




    // BiDirParent Methods




    /**
     * Find the BiDirChildren Collections (all fields of this parent annotated with {@link BiDirChildCollection} )
     * mapped to the Type of the Entities in the Collection.
     * @return
     */
    public Map<Collection<IdentifiableEntity>,Class<IdentifiableEntity>> findBiDirChildCollections(IdentifiableEntity parent){
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        return findEntityCollections(parent,BiDirChildCollection.class);
    }

    /**
     * Find the single BiDirChildren (all fields of this parent annotated with {@link BiDirChildEntity} and not null.
     * @return
     */
    public Set<IdentifiableEntity> findSingleBiDirChildren(IdentifiableEntity parent){
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        return findSingleEntities(parent, BiDirChildEntity.class);
    }

    /**
     * Add a new Child to this parent.
     * Call this, when saving a BiDirChild of this parent.
     * child will be added to fields with {@link BiDirChildCollection} and fields with {@link BiDirChildEntity} will be set with newChild, when most specific type matches of newChild matches the field.
     * Child wont be added and UnknownChildTypeException will be thrown when corresponding {@link BiDirChildCollection} is null.
     * @param newChild
     * @throws UnknownChildTypeException
     */
    public void linkBiDirChild(IdentifiableEntity parent, IdentifiableEntity newChild) throws UnknownChildTypeException{
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        assertEntityRelationType(newChild, RelationalEntityType.BiDirChild);
        linkEntity(parent, newChild, BiDirChildEntity.class, BiDirChildCollection.class);
    }

    /**
     * This parent wont know about the given biDirChildToRemove after this operation.
     * Call this, before you delete the biDirChildToRemove.
     * Case 1: Remove Child BiDirChild from all {@link BiDirChildCollection}s from this parent.
     * Case 2: Set {@link BiDirChildEntity}Field to null if child is not saved in a collection in this parent.
     * @param biDirChildToRemove
     * @throws UnknownChildTypeException
     */
    public void unlinkBiDirChild(IdentifiableEntity parent, IdentifiableEntity biDirChildToRemove) throws UnknownChildTypeException{
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        assertEntityRelationType(biDirChildToRemove, RelationalEntityType.BiDirChild);
        unlinkEntity(parent, biDirChildToRemove,BiDirChildEntity.class,BiDirChildCollection.class);
    }

    /**
     * All children BiDirChild of this parent wont know about this parent, after this operation.
     * Clear all {@link BiDirChildCollection}s of this parent.
     * Call this, before you want to delete this parent.
     * @throws UnknownParentTypeException
     */
    public void unlinkChildrensParent(IdentifiableEntity parent) throws UnknownParentTypeException{
        for(IdentifiableEntity child: findSingleBiDirChildren(parent)){
            unlinkBiDirParent(child,parent);
        }
        for(Map.Entry<Collection<IdentifiableEntity>,Class<IdentifiableEntity>> entry: findBiDirChildCollections(parent).entrySet()){
            Collection<IdentifiableEntity> childrenCollection = entry.getKey();
            for(IdentifiableEntity child: childrenCollection){
                unlinkBiDirParent(child,parent);
            }
            childrenCollection.clear();
        }
    }




    // UniDirParent Methods




    /**
     * Find the UniDirChildren Collections (all fields of this parent annotated with {@link UniDirChildCollection} and not null )
     * and the Type of the Entities in the Collection.
     *
     * @return
     */
    public Map<Collection<IdentifiableEntity>, Class<IdentifiableEntity>> findUniDirChildCollections(IdentifiableEntity parent)  {
        assertEntityRelationType(parent, RelationalEntityType.UniDirParent);
        return findEntityCollections(parent,UniDirChildCollection.class);
    }
    /**
     * Find the single UniDirChildren (all fields of this parent annotated with {@link UniDirChildEntity} and not null.
     * @return
     */
    public Set<IdentifiableEntity> findSingleUniDirChildren(IdentifiableEntity parent) {
        assertEntityRelationType(parent, RelationalEntityType.UniDirParent);
        return findSingleEntities(parent,UniDirChildEntity.class);
    }

    /**
     * Add a new Child to this parent.
     * Call this, when saving a UniDirChild of this parent.
     * child will be added to fields with {@link UniDirChildCollection} and fields with {@link UniDirChildEntity} will be set with newChild, when most specific type matches of newChild matches the field.
     * Child wont be added and UnknownChildTypeException will be thrown when corresponding {@link UniDirChildCollection} is null.
     *
     * @param newChild
     * @throws UnknownChildTypeException
     */
    public void linkUniDirChild(IdentifiableEntity parent,IdentifiableEntity newChild) throws UnknownChildTypeException{
        assertEntityRelationType(parent, RelationalEntityType.UniDirParent);
        linkEntity(parent,newChild,UniDirChildEntity.class,UniDirChildCollection.class);
    }

    /**
     * This parent wont know about the given uniDirChildToRemove after this operation.
     * Call this, before you delete the uniDirChildToRemove.
     * Case 1: Remove Child UniDirChild from all {@link UniDirChildCollection}s from this parent.
     * Case 2: Set {@link UniDirChildEntity}Field to null if child is not saved in a collection in this parent.
     *
     * @param toRemove
     * @throws UnknownChildTypeException
     */
    public void unlinkUniDirChild(IdentifiableEntity parent, IdentifiableEntity toRemove) throws UnknownChildTypeException{
        assertEntityRelationType(parent, RelationalEntityType.UniDirParent);
        unlinkEntity(parent,toRemove,UniDirChildEntity.class,UniDirChildCollection.class);
    }






    // CORE GENERIC METHODS 





    protected<C> Set<C> findSingleEntities(IdentifiableEntity<?> entity, Class<? extends Annotation> annotationClass){
        Set<C> entities = new HashSet<>();
        EntityReflectionUtils.doWithAnnotatedFields(annotationClass,entity.getClass(), field -> {
            C foundEntity = (C) field.get(entity);
            if(foundEntity == null){
                //skip
                return;
            }
            entities.add(foundEntity);
        });
        return entities;
    }

    /**
     * Find the Entity Collections (all fields of this annotated with @param entityAnnotationClass)
     * mapped to the Type of the Entities in the Collection.
     *
     * @return
     */
    protected<C> Map<Collection<C>, Class<C>> findEntityCollections(IdentifiableEntity entity, Class<? extends Annotation> entityAnnotationClass) {
        Map<Collection<C>, Class<C>> entityCollection_entityTypeMap = new HashMap<>();
        EntityReflectionUtils.doWithAnnotatedFields(entityAnnotationClass, entity.getClass(), field -> {
            Collection<C> entityCollection = (Collection<C>) field.get(entity);
            if (entityCollection == null) {
                //throw new IllegalArgumentException("Null idCollection found in BiDirParent "+ this + " for EntityCollectionField with name: " + field.getName());
                log.warn("Auto-generating Collection for null valued BiDirEntityCollection Field: " + field);
                Collection emptyCollection = CollectionUtils.createEmptyCollection(field);
                field.set(entity, emptyCollection);
                entityCollection = emptyCollection;
            }
            Class<C> entityType = (Class<C>) EntityAnnotationUtils.getEntityType(field.getAnnotation(entityAnnotationClass));
            entityCollection_entityTypeMap.put(entityCollection, entityType);
        });
        return entityCollection_entityTypeMap;
    }

    protected void linkEntity(IdentifiableEntity<?> entity, IdentifiableEntity newEntity, Class<? extends Annotation> entityAnnotationClass, Class<? extends Annotation> entityCollectionAnnotationClass) throws UnknownEntityTypeException {
        AtomicBoolean added = new AtomicBoolean(false);
        //add to matching entity collections
        // todo THIS CORRECT
        for (Map.Entry<Collection<IdentifiableEntity>, Class<IdentifiableEntity>> entry : this.<IdentifiableEntity>findEntityCollections(entity,entityCollectionAnnotationClass).entrySet()) {
            Class<? extends IdentifiableEntity> targetClass = entry.getValue();
            if (newEntity.getClass().equals(targetClass)) {
                (entry.getKey()).add(newEntity);
                added.set(true);
            }
        }
        //set matching entity
        EntityReflectionUtils.doWithAnnotatedFieldsOfType(newEntity.getClass(),entityAnnotationClass,entity.getClass(),entityField -> {
            IdentifiableEntity oldEntity = (IdentifiableEntity) entityField.get(entity);
            if (oldEntity != null) {
                log.warn("Overriding old entity: " + oldEntity + " with new entity " + newEntity + " of source sntity " + entity);
            }
            entityField.set(entity, newEntity);
            added.set(true);
        });
        if (!added.get()) {
            throw new UnknownEntityTypeException(entity.getClass(), newEntity.getClass());
        }
    }

    protected void unlinkEntity(IdentifiableEntity entity, IdentifiableEntity entityToRemove, Class<? extends Annotation> entityEntityAnnotationClass, Class<? extends Annotation> entityEntityCollectionAnnotationClass) throws UnknownEntityTypeException{
        AtomicBoolean deleted = new AtomicBoolean(false);
        for (Map.Entry<Collection<IdentifiableEntity>, Class<IdentifiableEntity>> entry : this.<IdentifiableEntity>findEntityCollections(entity,entityEntityCollectionAnnotationClass).entrySet()) {
            Collection<IdentifiableEntity> entityCollection = entry.getKey();
            if(entityCollection!=null){
                if(!entityCollection.isEmpty()){
                    Optional<IdentifiableEntity> optionalEntity = entityCollection.stream().findFirst();
                    if(optionalEntity.isPresent()){
                        IdentifiableEntity removeCandidate = optionalEntity.get();
                        if(entityToRemove.getClass().equals(removeCandidate.getClass())){
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
        EntityReflectionUtils.doWithAnnotatedFields(entityEntityAnnotationClass,entity.getClass(), entityField -> {
            IdentifiableEntity removeCandidate = (IdentifiableEntity) entityField.get(entity);
            if(removeCandidate!=null) {
                if (removeCandidate.getClass().equals(entityToRemove.getClass())) {
                    entityField.set(entity, null);
                    deleted.set(true);
                }
            }
        });
        if(!deleted.get()){
            System.err.println("it is also possible, that entity to unlink was not found in collection");
            throw new UnknownEntityTypeException(entity.getClass(), entityToRemove.getClass());
        }
    }




    // HELPER METHODS





    void assertEntityRelationType(IdentifiableEntity entity, RelationalEntityType expectedType){
        if (!inferTypes(entity.getClass()).contains(expectedType)){
            throw new IllegalArgumentException("Entity: " + entity + " is not of expected entity relation type: " + expectedType.name());
        }
    }
}
