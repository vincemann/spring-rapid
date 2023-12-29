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
import com.github.vincemann.springrapid.core.util.EntityReflectionUtils;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.vincemann.springrapid.core.util.ProxyUtils.*;
import static com.github.vincemann.springrapid.core.util.ProxyUtils.hibernateUnproxy;

@Slf4j
public class RapidRelationalEntityManagerUtil implements RelationalEntityManagerUtil {


    @Cacheable(value = "entityRelationTypes")
    @Override
    public Set<RelationalEntityType> inferTypes(Class<? extends IdentifiableEntity> entityClass) {
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
    // accepts proxies
    @Override
    public void linkBiDirParentsChild(IdentifiableEntity child, String... membersToCheck) {
        //set backreferences
        for (IdentifiableEntity parent : findAllBiDirParents(child, membersToCheck)) {
            linkBiDirChild(parent, child, membersToCheck);
        }
    }


    /**
     * Find the BiDirParent Collections (all fields of this parent annotated with {@link BiDirParentCollection} )
     * mapped to the Type of the Entities in the Collection.
     *
     * @return
     */
    // accepts proxies
    // returns collection of potential proxies
    public Map<Class<IdentifiableEntity>, Collection<IdentifiableEntity>> findBiDirParentCollections(IdentifiableEntity child, String... membersToCheck) {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        return findEntityCollections(child, BiDirParentCollection.class, membersToCheck);
    }

    /**
     * @return all parent of this, that are not null
     */
    // accepts proxies
    // returns collection of potential proxies
    public Collection<IdentifiableEntity> findSingleBiDirParents(IdentifiableEntity child, String... membersToCheck) {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        return findSingleEntities(child, BiDirParentEntity.class, membersToCheck);
    }

    // accepts proxies
    // returns collection of potential proxies
    public Collection<IdentifiableEntity> findAllBiDirParents(IdentifiableEntity child, String... membersToCheck) {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        return findAllEntities(child, BiDirParentEntity.class, BiDirParentCollection.class, membersToCheck);
    }


    // accepts proxies
    public void linkBiDirParent(IdentifiableEntity child, IdentifiableEntity parent, String... membersToCheck) throws UnknownParentTypeException {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        linkEntity(child, parent, BiDirParentEntity.class, BiDirParentCollection.class, membersToCheck);
    }


    // accepts proxies
    @Override
    public void unlinkBiDirParentsFrom(IdentifiableEntity child, String... membersToCheck) throws UnknownChildTypeException, UnknownParentTypeException {
        for (IdentifiableEntity parent : findAllBiDirParents(child, membersToCheck)) {
            if (parent != null) {
                unlinkBiDirParent(child, parent, membersToCheck);
            } else {
                log.warn("Parent Reference of BiDirChild with type: " + child.getClass().getSimpleName() + " was not set when deleting -> parent was deleted before child");
            }
        }
    }


    // accepts proxies
    public void unlinkBiDirParent(IdentifiableEntity child, IdentifiableEntity parent, String... membersToCheck) throws UnknownParentTypeException {
        assertEntityRelationType(child, RelationalEntityType.BiDirChild);
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        unlinkEntity(child, parent, BiDirParentEntity.class, BiDirParentCollection.class, membersToCheck);
    }


    // accepts proxies
    public void unlinkBiDirParentsChild(IdentifiableEntity child, String... membersToCheck) throws UnknownEntityTypeException {
        for (IdentifiableEntity parent : findAllBiDirParents(child, membersToCheck)) {
            unlinkBiDirChild(parent, child, membersToCheck);
        }
    }


    // BiDirParent Methods


    // accepts proxies
    public void linkBiDirChildrensParent(IdentifiableEntity parent, String... membersToCheck) {
        for (IdentifiableEntity child : findAllBiDirChildren(parent, membersToCheck)) {
            linkBiDirParent(child, parent, membersToCheck);
        }
    }

    /**
     * Find the BiDirChildren Collections (all fields of this parent annotated with {@link BiDirChildCollection} )
     * mapped to the Type of the Entities in the Collection.
     *
     * @return
     */
    // accepts proxies
    // returns collection of potential proxies
    public Map<Class<IdentifiableEntity>, Collection<IdentifiableEntity>> findBiDirChildCollections(IdentifiableEntity parent, String... membersToCheck) {
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        return findEntityCollections(parent, BiDirChildCollection.class, membersToCheck);
    }

    /**
     * Find the single BiDirChildren (all fields of this parent annotated with {@link BiDirChildEntity} and not null.
     *
     * @return
     */
    // accepts proxies
    // returns collection of potential proxies
    public Set<IdentifiableEntity> findSingleBiDirChildren(IdentifiableEntity parent, String... membersToCheck) {
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        return findSingleEntities(parent, BiDirChildEntity.class, membersToCheck);
    }

    // accepts proxies
    // returns collection of potential proxies
    public Collection<IdentifiableEntity> findAllBiDirChildren(IdentifiableEntity parent, String... membersToCheck) {
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        return findAllEntities(parent, BiDirChildEntity.class, BiDirChildCollection.class, membersToCheck);
    }


    // accepts proxies
    @Override
    public void linkBiDirChild(IdentifiableEntity parent, IdentifiableEntity childToSet, String... membersToCheck) throws UnknownChildTypeException {
        assertEntityRelationType(parent, RelationalEntityType.BiDirParent);
        assertEntityRelationType(childToSet, RelationalEntityType.BiDirChild);
        linkEntity(parent, childToSet, BiDirChildEntity.class, BiDirChildCollection.class, membersToCheck);
    }


    // accepts proxies
    public void unlinkBiDirChild(IdentifiableEntity parent, IdentifiableEntity childToDelete, String... membersToCheck) throws UnknownChildTypeException {
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
    // accepts proxies
    public void unlinkBiDirChildrensParent(IdentifiableEntity parent, String... membersToCheck) throws UnknownParentTypeException {
        for (IdentifiableEntity child : findAllBiDirChildren(parent, membersToCheck)) {
            unlinkBiDirParent(hibernateUnproxy(child), hibernateUnproxy(parent), membersToCheck);
        }
    }


    // UniDirParent Methods


    /**
     * Find the UniDirChildren Collections (all fields of this parent annotated with {@link UniDirChildCollection} and not null )
     * and the Type of the Entities in the Collection.
     *
     * @return
     */
    // accepts proxies
    // returns collection of potential proxies
    public Map<Class<IdentifiableEntity>, Collection<IdentifiableEntity>> findUniDirChildCollections(IdentifiableEntity parent, String... membersToCheck) {
        assertEntityRelationType(parent, RelationalEntityType.UniDirParent);
        return findEntityCollections(parent, UniDirChildCollection.class, membersToCheck);
    }

    /**
     * Find the single UniDirChildren (all fields of this parent annotated with {@link UniDirChildEntity} and not null.
     *
     * @return
     */
    // accepts proxies
    // returns collection of potential proxies
    public Set<IdentifiableEntity> findSingleUniDirChildren(IdentifiableEntity parent, String... membersToCheck) {
        assertEntityRelationType(parent, RelationalEntityType.UniDirParent);
        return findSingleEntities(parent, UniDirChildEntity.class, membersToCheck);
    }

    // accepts proxies
    // returns collection of potential proxies
    public Collection<IdentifiableEntity> findAllUniDirChildren(IdentifiableEntity child, String... membersToCheck) {
        assertEntityRelationType(child, RelationalEntityType.UniDirParent);
        return findAllEntities(child, UniDirChildEntity.class, UniDirChildCollection.class, membersToCheck);
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
    // accepts proxies
    public void linkUniDirChild(IdentifiableEntity parent, IdentifiableEntity newChild, String... membersToCheck) throws UnknownChildTypeException {
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
     * @throws UnknownChildTypeException
     */
    // accepts proxies
    public void unlinkUniDirChild(IdentifiableEntity parent, IdentifiableEntity toRemove, String... membersToCheck) throws UnknownChildTypeException {
        assertEntityRelationType(parent, RelationalEntityType.UniDirParent);
        unlinkEntity(parent, toRemove, UniDirChildEntity.class, UniDirChildCollection.class, membersToCheck);
    }


    // CORE GENERIC METHODS


    // wants proxied arg -> lazy loading properties is wanted
    // returns set of proxies
    protected <C> Set<C> findSingleEntities(IdentifiableEntity<?> entity, Class<? extends Annotation> annotationClass, String... membersToCheck) {
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
    // wants proxy arg bc field.get call should be loaded lazily - also setting of emtpy collection should be applied
    // returns collection of potential proxies
    protected <C> Map<Class<C>, Collection<C>> findEntityCollections(IdentifiableEntity entity, Class<? extends Annotation> entityAnnotationClass, String... membersToCheck) {
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
            Class<C> entityType = (Class<C>) EntityAnnotationUtils.getEntityType(field.getAnnotation(entityAnnotationClass));
            entityType_collectionMap.put(entityType, entityCollection);
        });
        return entityType_collectionMap;
    }


    // expects all args to be unproxied - wants proxies
    // returns collection of potential proxies
    public Collection<IdentifiableEntity> findAllEntities(IdentifiableEntity entity, Class<? extends Annotation> singleEntityAnnotation, Class<? extends Annotation> collectionEntityAnnotation, String... membersToCheck) {
        Set<IdentifiableEntity> relatedEntities = new HashSet<>();
        relatedEntities.addAll(findSingleEntities(entity, singleEntityAnnotation, membersToCheck));
        Map<Class<IdentifiableEntity>, Collection<IdentifiableEntity>> biDirParentCollections = findEntityCollections(entity, collectionEntityAnnotation, membersToCheck);
        for (Collection<IdentifiableEntity> relatedEntityCollections : biDirParentCollections.values()) {
            relatedEntities.addAll(relatedEntityCollections);
        }
        return relatedEntities;
    }


    // expects all args to be unproxied - wants proxies
    protected void linkEntity(IdentifiableEntity<?> entity, IdentifiableEntity newEntity, Class<? extends Annotation> entityAnnotationClass, Class<? extends Annotation> entityCollectionAnnotationClass, String... membersToCheck) throws UnknownEntityTypeException {
        AtomicBoolean added = new AtomicBoolean(false);
        //add to matching entity collections
        // todo THIS CORRECT
        for (Map.Entry<Class<IdentifiableEntity>, Collection<IdentifiableEntity>> entry : this.<IdentifiableEntity>findEntityCollections(entity, entityCollectionAnnotationClass, membersToCheck).entrySet()) {
            // entry always has unproxied correct class type (read from value of annotations)
            Class<? extends IdentifiableEntity> targetClass = entry.getKey();
            Collection<IdentifiableEntity> entityCollection = entry.getValue();
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
            IdentifiableEntity oldEntity = (IdentifiableEntity) fieldAccessor.getPropertyValue(entityField.getName());
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
            throw new UnknownEntityTypeException(entity.getClass(), newEntity.getClass());
        }
    }

    // wants proxies
    protected void unlinkEntity(IdentifiableEntity entity, IdentifiableEntity entityToRemove, Class<? extends Annotation> entityEntityAnnotationClass, Class<? extends Annotation> entityEntityCollectionAnnotationClass, String... membersToCheck) throws UnknownEntityTypeException {
        AtomicBoolean deleted = new AtomicBoolean(false);
//        IdentifiableEntity _entityToRemove = hibernateUnproxy(entityToRemove);
//        IdentifiableEntity _entity = hibernateUnproxy(entity);
        for (Map.Entry<Class<IdentifiableEntity>, Collection<IdentifiableEntity>> entry : this.<IdentifiableEntity>findEntityCollections(entity, entityEntityCollectionAnnotationClass, membersToCheck).entrySet()) {
            // should be a hibernate managed collection
            Collection<IdentifiableEntity> entityCollection = entry.getValue();
            if (entityCollection != null) {
                if (!entityCollection.isEmpty()) {
                    IdentifiableEntity removeCandidate = entityCollection.stream().findFirst().get();
                    if (getTargetClass(entityToRemove).equals(getTargetClass(removeCandidate))) {
                        // this set needs to remove the entity
                        // here is a hibernate bug in persistent set remove function, see https://stackoverflow.com/a/47968974
                        // therefor we use an odd workaround
                        Iterator<IdentifiableEntity> iterator = entityCollection.iterator();
                        while (iterator.hasNext()) {
                            IdentifiableEntity e = iterator.next();
                            // hibernate proxy should call my custom equals method, but that also compares class, so unproxy
                            if (ProxyUtils.hibernateEquals(e,entityToRemove)) {
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
        EntityReflectionUtils.doWithAnnotatedNamedFields(entityEntityAnnotationClass, getTargetClass(entity), Sets.newHashSet(membersToCheck), entityField -> {
            PropertyAccessor fieldAccessor = PropertyAccessorFactory.forBeanPropertyAccess(entity);
            IdentifiableEntity removeCandidate = (IdentifiableEntity) fieldAccessor.getPropertyValue(entityField.getName());
//            IdentifiableEntity removeCandidate = hibernateUnproxy((IdentifiableEntity) entityField.get(entity));
            if (removeCandidate != null) {
                if (ProxyUtils.unproxyEquals(removeCandidate, entityToRemove)) {
                    fieldAccessor.setPropertyValue(entityField.getName(),null);
//                    entityField.set(entity, null);
                    deleted.set(true);
                }
            }
        });
        if (!deleted.get()) {
            System.err.println("it is also possible, that entity to unlink was not found in collection");
            throw new UnknownEntityTypeException(entity.getClass(), entityToRemove.getClass());
        }
    }


    // HELPER METHODS


    // accepts proxies
    void assertEntityRelationType(IdentifiableEntity entity, RelationalEntityType expectedType) {
        if (!inferTypes(getTargetClass(entity)).contains(expectedType)) {
            throw new IllegalArgumentException("Entity: " + entity + " is not of expected entity relation type: " + expectedType.name());
        }
    }
}
