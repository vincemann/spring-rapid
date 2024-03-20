package com.github.vincemann.springrapid.autobidir.entity;

import com.github.vincemann.springrapid.autobidir.AutoHandleEntityRelationShipException;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildEntity;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.UniDirChildCollection;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.UniDirChildEntity;
import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentCollection;
import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentEntity;
import com.github.vincemann.springrapid.autobidir.util.RelationalEntityAnnotationUtils;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.util.EntityReflectionUtils;
import com.github.vincemann.springrapid.core.util.HibernateProxyUtils;
import com.google.common.collect.Sets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.UnknownEntityTypeException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.vincemann.springrapid.core.util.HibernateProxyUtils.getTargetClass;


public class RelationalEntityManagerUtilImpl implements RelationalEntityManagerUtil {

    private final Log log = LogFactory.getLog(RelationalEntityManagerUtilImpl.class);


    @Cacheable(value = "entityRelationTypesCache")
    @Override
    public Set<RelationalEntityType> inferTypes(Class<? extends IdAwareEntity> entityClass) {
        Set<RelationalEntityType> relationalEntityTypes = new HashSet<>();
        org.springframework.util.ReflectionUtils.doWithFields(entityClass, field -> {
            org.springframework.util.ReflectionUtils.makeAccessible(field);
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {

                if (annotation.annotationType().equals(BiDirChildEntity.class)) {
                    relationalEntityTypes.add(RelationalEntityType.BiDirParent);
                }
                if (annotation.annotationType().equals(BiDirChildCollection.class)) {
                    relationalEntityTypes.add(RelationalEntityType.BiDirParent);
                }


                if (annotation.annotationType().equals(UniDirChildEntity.class)) {
                    relationalEntityTypes.add(RelationalEntityType.UniDirParent);
                }
                if (annotation.annotationType().equals(UniDirChildCollection.class)) {
                    relationalEntityTypes.add(RelationalEntityType.UniDirParent);
                }


                if (annotation.annotationType().equals(BiDirParentEntity.class)) {
                    relationalEntityTypes.add(RelationalEntityType.BiDirChild);
                }
                if (annotation.annotationType().equals(BiDirParentCollection.class)) {
                    relationalEntityTypes.add(RelationalEntityType.BiDirChild);
                }
            }
            // dont apply filter bc we also need to check for sets of IdentifiableEntities
        }/*, field -> IdentifiableEntity.class.isAssignableFrom(field.getType())*/);
        return relationalEntityTypes;
    }


    // BiDirChild methods


    /**
     * finds all parents of
     *
     * @poram child and links child to parent
     * -> set backreference of child
     */
    @Override
    public void linkBiDirParentsChild(IdAwareEntity child, String... membersToCheck) {
        //set backreferences
        for (IdAwareEntity parent : findAllBiDirParents(child, membersToCheck)) {
            linkBiDirChild(parent, child, membersToCheck);
        }
    }


    /**
     * Find the BiDirParent Collections (all fields of this parent annotated with {@link BiDirParentCollection} )
     * mapped to the Type of the Entities in the Collection.
     *
     */
    @Override
    public Map<Class<IdAwareEntity>, Collection<IdAwareEntity>> findBiDirParentCollections(IdAwareEntity child, String... membersToCheck) {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        return findEntityCollections(child, BiDirParentCollection.class, membersToCheck);
    }

    /**
     * @return all parents of this, that are not null
     */
    @Override
    public Collection<IdAwareEntity> findSingleBiDirParents(IdAwareEntity child, String... membersToCheck) {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        return findSingleEntities(child, BiDirParentEntity.class, membersToCheck);
    }

    @Override
    public Collection<IdAwareEntity> findAllBiDirParents(IdAwareEntity child, String... membersToCheck) {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        return findAllEntities(child, BiDirParentEntity.class, BiDirParentCollection.class, membersToCheck);
    }


    @Override
    public void linkBiDirParent(IdAwareEntity child, IdAwareEntity parent, String... membersToCheck) throws AutoHandleEntityRelationShipException {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        linkEntity(child, parent, BiDirParentEntity.class, BiDirParentCollection.class, membersToCheck);
    }


    @Override
    public void unlinkBiDirParentsFrom(IdAwareEntity child, String... membersToCheck) throws AutoHandleEntityRelationShipException {
        for (IdAwareEntity parent : findAllBiDirParents(child, membersToCheck)) {
            if (parent != null) {
                unlinkBiDirParent(child, parent, membersToCheck);
            } else {
                log.warn("Parent Reference of BiDirChild with type: " + child.getClass().getSimpleName() + " was not set when deleting -> parent was deleted before child");
            }
        }
    }


    @Override
    public void unlinkBiDirParent(IdAwareEntity child, IdAwareEntity parent, String... membersToCheck) throws AutoHandleEntityRelationShipException {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        unlinkEntity(child, parent, BiDirParentEntity.class, BiDirParentCollection.class, membersToCheck);
    }


    @Override
    public void unlinkBiDirParentsChild(IdAwareEntity child, String... membersToCheck) throws UnknownEntityTypeException {
        for (IdAwareEntity parent : findAllBiDirParents(child, membersToCheck)) {
            unlinkBiDirChild(parent, child, membersToCheck);
        }
    }


    // BiDirParent Methods
    @Override
    public void linkBiDirChildrensParent(IdAwareEntity parent, String... membersToCheck) {
        for (IdAwareEntity child : findAllBiDirChildren(parent, membersToCheck)) {
            linkBiDirParent(child, parent, membersToCheck);
        }
    }

    /**
     * Find the BiDirChildren Collections (all fields of this parent annotated with {@link BiDirChildCollection} )
     * mapped to the Type of the Entities in the Collection.
     *
     * @return
     */
    @Override
    public Map<Class<IdAwareEntity>, Collection<IdAwareEntity>> findBiDirChildCollections(IdAwareEntity parent, String... membersToCheck) {
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        return findEntityCollections(parent, BiDirChildCollection.class, membersToCheck);
    }

    /**
     * Find the single BiDirChildren (all fields of this parent annotated with {@link BiDirChildEntity} and not null.
     *
     * @return
     */
    @Override
    public Set<IdAwareEntity> findSingleBiDirChildren(IdAwareEntity parent, String... membersToCheck) {
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        return findSingleEntities(parent, BiDirChildEntity.class, membersToCheck);
    }

    @Override
    public Collection<IdAwareEntity> findAllBiDirChildren(IdAwareEntity parent, String... membersToCheck) {
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        return findAllEntities(parent, BiDirChildEntity.class, BiDirChildCollection.class, membersToCheck);
    }


    @Override
    public void linkBiDirChild(IdAwareEntity parent, IdAwareEntity childToSet, String... membersToCheck) throws AutoHandleEntityRelationShipException {
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        assertEntityRelationType(childToSet, RelationalEntityType.BiDirChild);
        linkEntity(parent, childToSet, BiDirChildEntity.class, BiDirChildCollection.class, membersToCheck);
    }


    public void unlinkBiDirChild(IdAwareEntity parent, IdAwareEntity childToDelete, String... membersToCheck) throws AutoHandleEntityRelationShipException {
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        assertEntityRelationType(childToDelete, RelationalEntityType.BiDirChild);
        unlinkEntity(parent, childToDelete, BiDirChildEntity.class, BiDirChildCollection.class, membersToCheck);
    }

    /**
     * find all children of
     *
     * @param parent and unlink it from them
     *               -> remove childrens backreference
     */
    public void unlinkBiDirChildrensParent(IdAwareEntity parent, String... membersToCheck) throws AutoHandleEntityRelationShipException {
        for (IdAwareEntity child : findAllBiDirChildren(parent, membersToCheck)) {
            unlinkBiDirParent(child, parent, membersToCheck);
        }
    }


    // UniDirParent Methods


    /**
     * Find the UniDirChildren Collections (all fields of this parent annotated with {@link UniDirChildCollection} and not null )
     * and the Type of the Entities in the Collection.
     *
     * @return
     */
    public Map<Class<IdAwareEntity>, Collection<IdAwareEntity>> findUniDirChildCollections(IdAwareEntity parent, String... membersToCheck) {
        assertEntityRelationType(parent, RelationalEntityType.UniDirParent);
        return findEntityCollections(parent, UniDirChildCollection.class, membersToCheck);
    }

    /**
     * Find the single UniDirChildren (all fields of this parent annotated with {@link UniDirChildEntity} and not null.
     *
     * @return
     */
    public Set<IdAwareEntity> findSingleUniDirChildren(IdAwareEntity parent, String... membersToCheck) {
        assertEntityRelationType(parent, RelationalEntityType.UniDirParent);
        return findSingleEntities(parent, UniDirChildEntity.class, membersToCheck);
    }

    public Collection<IdAwareEntity> findAllUniDirChildren(IdAwareEntity child, String... membersToCheck) {
        assertEntityRelationType(child, RelationalEntityType.UniDirParent);
        return findAllEntities(child, UniDirChildEntity.class, UniDirChildCollection.class, membersToCheck);
    }

    /**
     * Add a new Child to this parent.
     * Call this, when saving a UniDirChild of this parent.
     * child will be added to fields with {@link UniDirChildCollection} and fields with {@link UniDirChildEntity} will be set with newChild, when most specific type matches of newChild matches the field.
     * Child wont be added and {code AutoHandleEntityRelationShipException} will be thrown when corresponding {@link UniDirChildCollection} is null.
     *
     * @param newChild
     * @throws AutoHandleEntityRelationShipException
     */
    public void linkUniDirChild(IdAwareEntity parent, IdAwareEntity newChild, String... membersToCheck) throws AutoHandleEntityRelationShipException {
        assertEntityRelationType(parent, RelationalEntityType.UniDirParent);
        linkEntity(parent, newChild, UniDirChildEntity.class, UniDirChildCollection.class, membersToCheck);
    }

    /**
     * This parent wont know about the given uniDirChildToRemove after this operation.
     * Call this, before you delete the uniDirChildToRemove.
     * Case 1: Remove Child UniDirChild from all {@link UniDirChildCollection}s from this parent.
     * Case 2: Set {@link UniDirChildEntity}Field to null if child is not saved in a collection in this parent.
     *
     * @param toRemove
     */
    public void unlinkUniDirChild(IdAwareEntity parent, IdAwareEntity toRemove, String... membersToCheck) throws AutoHandleEntityRelationShipException {
        assertEntityRelationType(parent, RelationalEntityType.UniDirParent);
        unlinkEntity(parent, toRemove, UniDirChildEntity.class, UniDirChildCollection.class, membersToCheck);
    }


    // CORE GENERIC METHODS

    protected <C> Set<C> findSingleEntities(IdAwareEntity<?> entity, Class<? extends Annotation> annotationClass, String... membersToCheck) {
        Set<C> entities = new HashSet<>();
        EntityReflectionUtils.doWithAnnotatedNamedFields(annotationClass, getTargetClass(entity), Sets.newHashSet(membersToCheck), field -> {
            PropertyAccessor fieldAccessor = PropertyAccessorFactory.forBeanPropertyAccess(entity);
            C foundEntity = (C) fieldAccessor.getPropertyValue(field.getName());
//            C foundEntity = (C) field.get(entity);
            if (foundEntity == null) {
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
    protected <C> Map<Class<C>, Collection<C>> findEntityCollections(IdAwareEntity entity, Class<? extends Annotation> entityAnnotationClass, String... membersToCheck) {
        Map<Class<C>, Collection<C>> entityType_collectionMap = new HashMap<>();

        // iterate over unproxied entity fields
        EntityReflectionUtils.doWithAnnotatedNamedFields(entityAnnotationClass, getTargetClass(entity), Sets.newHashSet(membersToCheck), field -> {
            // always use getter and setter
            PropertyAccessor fieldAccessor = PropertyAccessorFactory.forBeanPropertyAccess(entity);
            Collection<C> entityCollection = (Collection<C>) fieldAccessor.getPropertyValue(field.getName());
            if (entityCollection == null) {
                // must use PersistentSet ect for lazy loading, different then for eager loading
                // cant do the right thing here in all cases -> dont support, user has to always make sure sets are created
//                throw new IllegalArgumentException("entity collection field must not be null: " + getTargetClass(entity) + "->" + field.getName());

                // just ignore the property when set to null -> need that for partial update stuff
                // null is the only indication, that I dont want to update that field
                if (log.isWarnEnabled())
                    log.warn("ignoring null collection: " + getTargetClass(entity) + " -> " + field.getName());
                return;

//                log.warn("Auto-generating Collection for null valued BiDirEntityCollection Field: " + field);
//                Collection emptyCollection = CollectionUtils.createEmptyCollection(field);
                // if ever changing back, use setter method here and not field.set
//                field.set(entity, emptyCollection);
//                entityCollection = emptyCollection;
            }
            Class<C> entityType = (Class<C>) RelationalEntityAnnotationUtils.getEntityType(field.getAnnotation(entityAnnotationClass));
            entityType_collectionMap.put(entityType, entityCollection);
        });
        return entityType_collectionMap;
    }


    public Collection<IdAwareEntity> findAllEntities(IdAwareEntity entity, Class<? extends Annotation> singleEntityAnnotation, Class<? extends Annotation> collectionEntityAnnotation, String... membersToCheck) {
        Set<IdAwareEntity> relatedEntities = new HashSet<>();
        relatedEntities.addAll(findSingleEntities(entity, singleEntityAnnotation, membersToCheck));
        Map<Class<IdAwareEntity>, Collection<IdAwareEntity>> biDirParentCollections = findEntityCollections(entity, collectionEntityAnnotation, membersToCheck);
        for (Collection<IdAwareEntity> relatedEntityCollections : biDirParentCollections.values()) {
            relatedEntities.addAll(relatedEntityCollections);
        }
        return relatedEntities;
    }


    protected void linkEntity(IdAwareEntity<?> entity, IdAwareEntity newEntity, Class<? extends Annotation> entityAnnotationClass, Class<? extends Annotation> entityCollectionAnnotationClass, String... membersToCheck) {
        AtomicBoolean added = new AtomicBoolean(false);
        //add to matching entity collections
        for (Map.Entry<Class<IdAwareEntity>, Collection<IdAwareEntity>> entry : this.<IdAwareEntity>findEntityCollections(entity, entityCollectionAnnotationClass, membersToCheck).entrySet()) {
            // entry always has unproxied correct class type (read from value of annotations)
            Class<? extends IdAwareEntity> targetClass = entry.getKey();
            Collection<IdAwareEntity> entityCollection = entry.getValue();
            if (getTargetClass(newEntity).equals(targetClass)) {
                // collection should be hibernate persistent set -> add method called on this type will result in hibernate magic
                entityCollection.add(newEntity);
                added.set(true);
            }
        }
        //set matching entity
        // use getTargetClass for first arg, because field.getType() will be matched -> need the real entity class not proxy
        EntityReflectionUtils.doWithNamedAnnotatedFieldsOfType(getTargetClass(newEntity), entityAnnotationClass, entity.getClass(), Sets.newHashSet(membersToCheck), entityField -> {
            PropertyAccessor fieldAccessor = PropertyAccessorFactory.forBeanPropertyAccess(entity);
            // I want to call the getter here on the proxy to also trigger hibernate proxy methods for initialization
            IdAwareEntity oldEntity = (IdAwareEntity) fieldAccessor.getPropertyValue(entityField.getName());
            if (oldEntity != null) {
                log.warn("Overriding old entity: " + oldEntity + " with new entity " + newEntity + " of source sntity " + entity);
            }
            // both hibernate proxies -> hibernate updates relation ship
            // operating on field of proxy class, not entity class !
            // this way hibernate actually initializes this field
            // does not invoke the setter method, resulting in not initializing! use fieldAccessor to call setter
//              entityField.set(entity, newEntity);
            fieldAccessor.setPropertyValue(entityField.getName(), newEntity);

            // also does not invoke setter
//                BeanUtils.setProperty(entity,entityField.getName(),newEntity);

            added.set(true);

        });
        if (!added.get()) {
            throw new AutoHandleEntityRelationShipException("Error while trying to link entity " + newEntity + " to " + entity + ". " + entity.getClass().getSimpleName() + " does not have annotated field containing entities of type: " + newEntity.getClass());
        }
    }

    // proxies ok
    protected void unlinkEntity(IdAwareEntity entity, IdAwareEntity entityToRemove, Class<? extends Annotation> entityEntityAnnotationClass, Class<? extends Annotation> entityEntityCollectionAnnotationClass, String... membersToCheck) throws UnknownEntityTypeException {
        AtomicBoolean deleted = new AtomicBoolean(false);
        for (Map.Entry<Class<IdAwareEntity>, Collection<IdAwareEntity>> entry : this.<IdAwareEntity>findEntityCollections(entity, entityEntityCollectionAnnotationClass, membersToCheck).entrySet()) {
            // should be a hibernate managed collection
            Collection<IdAwareEntity> entityCollection = entry.getValue();
            if (entityCollection != null) {
                if (!entityCollection.isEmpty()) {
                    IdAwareEntity removeCandidate = entityCollection.stream().findFirst().get();
                    if (getTargetClass(entityToRemove).equals(getTargetClass(removeCandidate))) {
                        // this set needs to remove the entity
                        // here is a hibernate bug in persistent set remove function, see https://stackoverflow.com/a/47968974
                        // therefor we use an odd workaround
                        Iterator<IdAwareEntity> iterator = entityCollection.iterator();
                        while (iterator.hasNext()) {
                            IdAwareEntity e = iterator.next();
                            // hibernate proxy should call my custom equals method, but that also compares class, so unproxy
                            if (HibernateProxyUtils.jpaEquals(e,entityToRemove)) {
                                iterator.remove();
                                deleted.set(true);
                                break;
                            }
                        }
                    }
                }
            }
        }
        EntityReflectionUtils.doWithAnnotatedNamedFields(entityEntityAnnotationClass, getTargetClass(entity), Sets.newHashSet(membersToCheck), entityField -> {
            PropertyAccessor fieldAccessor = PropertyAccessorFactory.forBeanPropertyAccess(entity);
            IdAwareEntity removeCandidate = (IdAwareEntity) fieldAccessor.getPropertyValue(entityField.getName());
//            IdentifiableEntity removeCandidate = hibernateUnproxy((IdentifiableEntity) entityField.get(entity));
            if (removeCandidate != null) {
                if (HibernateProxyUtils.unproxyEquals(removeCandidate, entityToRemove)) {
                    fieldAccessor.setPropertyValue(entityField.getName(),null);
//                    entityField.set(entity, null);
                    deleted.set(true);
                }
            }
        });
        if (!deleted.get()) {
            if (log.isErrorEnabled())
                log.error("it is also possible, that entity to unlink was not found in collection");
            throw new AutoHandleEntityRelationShipException("Error while trying to unlink entity " + entityToRemove + " from " + entity + ". " + entity.getClass().getSimpleName() + " does not have annotated field containing entities of type: " + entityToRemove.getClass()
            + ", or entity to unlink not found in source entity."
            );
        }
    }


    // HELPER METHODS


    void assertEntityRelationType(IdAwareEntity entity, RelationalEntityType expectedType) {
        if (!inferTypes(getTargetClass(entity)).contains(expectedType)) {
            throw new IllegalArgumentException("Entity: " + entity + " is not of expected entity relation type: " + expectedType.name());
        }
    }
}
