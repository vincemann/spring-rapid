package io.github.vincemann.springrapid.entityrelationship.model.uniDir.parent;

import io.github.vincemann.springrapid.core.config.ReflectionUtilsBean;
import io.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import io.github.vincemann.springrapid.entityrelationship.model.uniDir.UniDirEntity;
import io.github.vincemann.springrapid.entityrelationship.model.uniDir.child.UniDirChild;
import io.github.vincemann.springrapid.entityrelationship.model.uniDir.child.UniDirChildCollection;
import io.github.vincemann.springrapid.entityrelationship.model.uniDir.child.UniDirChildEntity;
import io.github.vincemann.springrapid.entityrelationship.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Same as {@link io.github.vincemann.springrapid.entityrelationship.model.biDir.parent.BiDirParent} but for a unidirectional relationship.
 */
public interface UniDirParent extends UniDirEntity {
    Logger log = LoggerFactory.getLogger(UniDirParent.class);


    /**
     * Add a new Child to this parent.
     * Call this, when saving a {@link UniDirChild} of this parent.
     * child will be added to fields with {@link UniDirChildCollection} and fields with {@link UniDirChildEntity} will be set with newChild, when most specific type matches of newChild matches the field.
     * Child wont be added and UnknownChildTypeException will be thrown when corresponding {@link UniDirChildCollection} is null.
     *
     * @param newChild
     * @throws UnknownChildTypeException
     * @throws IllegalAccessException
     */
    default void _addChild(Object newChild) throws UnknownChildTypeException, IllegalAccessException {
        AtomicBoolean addedChild = new AtomicBoolean(false);
        for (Map.Entry entry : _getChildrenCollections().entrySet()) {
            Class targetClass = (Class) entry.getValue();
            if (newChild.getClass().equals(targetClass)) {
                ((Collection) entry.getKey()).add(newChild);
                addedChild.set(true);
            }
        }

        Field[] entityFields = _findChildrenEntityFields();
        for (Field childField : entityFields) {
            if (childField.getType().equals(newChild.getClass())) {
                childField.setAccessible(true);
                Object oldChild = childField.get(this);
                if (oldChild != null) {
                    log.warn("Overriding old child: " + oldChild + " with new Child: " + newChild + " of parent: " + this);
                }
                childField.set(this, newChild);
                addedChild.set(true);
            }
        }

        if (!addedChild.get()) {
            throw new UnknownChildTypeException(getClass(), newChild.getClass());
        }
    }

    /*default Field[] findChildrenCollectionFields(){
        return ReflectionUtils.getAnnotatedDeclaredFieldsAssignableFrom(this.getClass(),UniDirChildSet.class, Collection.class,true);
    }

    default Collection<Collection> getChildrenCollections(Field[] childrenSetFields) throws IllegalAccessException {
        Collection<Collection> childrenCollections = new ArrayList<>();
        for(Field childrenSetField : childrenSetFields){
            childrenSetField.setAccessible(true);
            Collection childrenCollection = (Collection) childrenSetField.get(this);
            childrenCollections.add(childrenCollection);
        }
        return childrenCollections;
    }*/

    /**
     * This parent wont know about the given uniDirChildToRemove after this operation.
     * Call this, before you delete the uniDirChildToRemove.
     * Case 1: Remove Child {@link UniDirChild} from all {@link UniDirChildCollection}s from this parent.
     * Case 2: Set {@link UniDirChildEntity}Field to null if child is not saved in a collection in this parent.
     *
     * @param uniDirChildToRemove
     * @throws UnknownChildTypeException
     * @throws IllegalAccessException
     */
    default void _dismissChild(Object uniDirChildToRemove) throws UnknownChildTypeException, IllegalAccessException {
        AtomicBoolean deletedChild = new AtomicBoolean(false);
        for (Map.Entry<Collection, Class<? extends UniDirChild>> entry : _getChildrenCollections().entrySet()) {
            Collection<? extends UniDirChild> childrenCollection = entry.getKey();
            if (childrenCollection != null) {
                if (!childrenCollection.isEmpty()) {
                    Optional<? extends UniDirChild> optionalUniDirChild = childrenCollection.stream().findFirst();
                    optionalUniDirChild.ifPresent(child -> {
                        if (uniDirChildToRemove.getClass().equals(child.getClass())) {
                            //this set needs to remove the child
                            boolean successfulRemove = childrenCollection.remove(uniDirChildToRemove);
                            if (!successfulRemove) {
                                log.warn("UniDirChild: " + uniDirChildToRemove + " was not present in children set of parent: " + this + " when trying to delete.");
                            } else {
                                deletedChild.set(true);
                            }
                        }
                    });
                }
            }
        }
        for (Field childField : _findChildrenEntityFields()) {
            childField.setAccessible(true);
            UniDirChild child = (UniDirChild) childField.get(this);
            if (child != null) {
                if (child.getClass().equals(uniDirChildToRemove.getClass())) {
                    childField.set(this, null);
                    deletedChild.set(true);
                }
            }
        }
        if (!deletedChild.get()) {
            throw new UnknownChildTypeException(this.getClass(), uniDirChildToRemove.getClass());
        }
    }

    default Field[] _findChildrenCollectionFields() {
        Field[] childrenCollectionFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(this.getClass(), UniDirChildCollection.class);
        return childrenCollectionFields;
    }

    default Field[] _findChildrenEntityFields() {
        Field[] childEntityFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(this.getClass(), UniDirChildEntity.class);
        return childEntityFields;
    }

    /**
     * Find the UniDirChildren Collections (all fields of this parent annotated with {@link UniDirChildCollection} and not null )
     * and the Type of the Entities in the Collection.
     *
     * @return
     */
    default Map<Collection, Class<? extends UniDirChild>> _getChildrenCollections() throws IllegalAccessException {
        Map<Collection, Class<? extends UniDirChild>> childrenCollection_CollectionTypeMap = new HashMap<>();
        Field[] collectionFields = _findChildrenCollectionFields();

        for (Field field : collectionFields) {
            field.setAccessible(true);
            Collection uniDirChildren = (Collection) field.get(this);
            if (uniDirChildren == null) {
                //throw new IllegalArgumentException("Null idCollection found in UniDirParent "+ this + " for ChildCollectionField with name: " + field.getName());
                log.warn("Auto-generating Collection for nullIdCollection Field: " + field);
                Collection emptyCollection = CollectionUtils.createEmptyCollection(field);
                field.set(this, emptyCollection);
                uniDirChildren = emptyCollection;
            }
            Class<? extends UniDirChild> collectionEntityType = field.getAnnotation(UniDirChildCollection.class).value();
            childrenCollection_CollectionTypeMap.put(uniDirChildren, collectionEntityType);
        }

        return childrenCollection_CollectionTypeMap;
    }


    /**
     * Find the single UniDirChildren (all fields of this parent annotated with {@link UniDirChildEntity} and not null.
     *
     * @return
     * @throws IllegalAccessException
     */
    default Set _getChildren() throws IllegalAccessException {
        Set children = new HashSet<>();
        Field[] entityFields = _findChildrenEntityFields();
        for (Field field : entityFields) {
            field.setAccessible(true);
            Object child = field.get(this);
            if (child == null) {
                //skip
                continue;
            }
            children.add(child);
        }
        return children;
    }
}
