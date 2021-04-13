package com.github.vincemann.springrapid.entityrelationship.model.parent;

import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.child.DirChild;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.util.CollectionUtils;
import com.github.vincemann.springrapid.entityrelationship.util.EntityAnnotationUtils;
import com.github.vincemann.springrapid.entityrelationship.util.EntityReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public interface DirParent {
    Logger log = LoggerFactory.getLogger(DirParent.class);

    /**
     * Find the BiDirChildren Collections (all fields of this parent annotated with {@link BiDirChildCollection} )
     * and the Type of the Entities in the Collection.
     *
     * @return
     */
    default <C> Map<Collection<C>, Class<C>> findAllChildCollections(Class<? extends Annotation> childEntityAnnotationClass) {
        Map<Collection<C>, Class<C>> childCollection_childTypeMap = new HashMap<>();
        EntityReflectionUtils.doWithAnnotatedFields(childEntityAnnotationClass, getClass(), field -> {
            Collection<C> children = (Collection<C>) field.get(this);
            if (children == null) {
                //throw new IllegalArgumentException("Null idCollection found in BiDirParent "+ this + " for ChildCollectionField with name: " + field.getName());
                log.warn("Auto-generating Collection for null valued BiDirChildCollection Field: " + field);
                Collection emptyCollection = CollectionUtils.createEmptyCollection(field);
                field.set(this, emptyCollection);
                children = emptyCollection;
            }
            Class<C> childType = (Class<C>) EntityAnnotationUtils.getEntityType(field.getAnnotation(childEntityAnnotationClass));
            childCollection_childTypeMap.put(children, childType);
        });
        return childCollection_childTypeMap;
    }

    default void linkChild(DirChild newChild, Class<? extends Annotation> childEntityAnnotationClass, Class<? extends Annotation> childEntityCollectionAnnotationClass) throws UnknownChildTypeException {
        AtomicBoolean added = new AtomicBoolean(false);
        //add to matching child collections
        for (Map.Entry<Collection<DirChild>, Class<DirChild>> entry : this.<DirChild>findAllChildCollections(childEntityCollectionAnnotationClass).entrySet()) {
            Class<? extends DirChild> targetClass = entry.getValue();
            if (newChild.getClass().equals(targetClass)) {
                (entry.getKey()).add(newChild);
                added.set(true);
            }
        }
        //set matching child
        EntityReflectionUtils.doWithAnnotatedFieldsOfType(newChild.getClass(),childEntityAnnotationClass,getClass(),childField -> {
            DirChild oldChild = (DirChild) childField.get(this);
            if (oldChild != null) {
                log.warn("Overriding old child: " + oldChild + " with new Child " + newChild + " of parent " + this);
            }
            childField.set(this, newChild);
            added.set(true);
        });
        if (!added.get()) {
            throw new UnknownChildTypeException(getClass(), newChild.getClass());
        }
    }

    default void unlinkChild(DirChild childToRemove, Class<? extends Annotation> childEntityAnnotationClass, Class<? extends Annotation> childEntityCollectionAnnotationClass) throws UnknownChildTypeException{
        AtomicBoolean deleted = new AtomicBoolean(false);
        for (Map.Entry<Collection<DirChild>, Class<DirChild>> entry : this.<DirChild>findAllChildCollections(childEntityCollectionAnnotationClass).entrySet()) {
            Collection<DirChild> childrenCollection = entry.getKey();
            if(childrenCollection!=null){
                if(!childrenCollection.isEmpty()){
                    Optional<DirChild> optionalBiDirChild = childrenCollection.stream().findFirst();
                    if(optionalBiDirChild.isPresent()){
                        DirChild child = optionalBiDirChild.get();
                        if(childToRemove.getClass().equals(child.getClass())){
                            //this set needs to remove the child
                            //here is a hibernate bug in persistent set remove function, see https://stackoverflow.com/a/47968974
                            //therefor we user removeAll as workaround
                            List<DirChild> toRemove = new ArrayList<>();
                            toRemove.add(childToRemove);
                            boolean successfulRemove = childrenCollection.removeAll(toRemove);
                            //childrenCollection = PersistentSet
                            if(!successfulRemove){
                                throw new RuntimeException("DirChild: "+toRemove+", which should be deleted from parents Child-collection, was not present in it or delete operation was not successful. "+this);
                            }else {
                                deleted.set(true);
                            }
                        }
                    }
                }
            }
        }
        EntityReflectionUtils.doWithAnnotatedFields(childEntityAnnotationClass,getClass(),childField -> {
            DirChild child = (DirChild) childField.get(this);
            if(child!=null) {
                if (child.getClass().equals(childToRemove.getClass())) {
                    childField.set(this, null);
                    deleted.set(true);
                }
            }
        });
        if(!deleted.get()){
            throw new UnknownChildTypeException(this.getClass(), childToRemove.getClass());
        }
    }

    default <C extends DirChild> Set<C> findSingleChildren(Class<? extends Annotation> childEntityAnnotationClass){
        Set<C> children = new HashSet<>();
        EntityReflectionUtils.doWithAnnotatedFields(childEntityAnnotationClass,getClass(),field -> {
            C child = (C) field.get(this);
            if(child == null){
                //skip
                return;
            }
            children.add(child);
        });
        return children;
    }
}
