package com.github.vincemann.springrapid.entityrelationship.model.parent;

import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.child.DirChild;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.util.CollectionUtils;
import com.github.vincemann.springrapid.entityrelationship.util.EntityAnnotationUtils;
import com.github.vincemann.springrapid.entityrelationship.util.EntityReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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

//        Field[] collectionFields =findChildrenCollectionFields();
//        for(Field field : collectionFields){
//            field.setAccessible(true);
//            Collection<? extends BiDirChild> biDirChildren = (Collection<? extends BiDirChild>) field.get(this);
//            if(biDirChildren == null){
//                //throw new IllegalArgumentException("Null idCollection found in BiDirParent "+ this + " for ChildCollectionField with name: " + field.getName());
//                log.warn("Auto-generating Collection for nullIdCollection Field: " + field);
//                Collection emptyCollection = CollectionUtils.createEmptyCollection(field);
//                field.set(this,emptyCollection);
//                biDirChildren=emptyCollection;
//            }
//            Class<? extends BiDirChild> collectionEntityType = field.getAnnotation(BiDirChildCollection.class).value();
//            childCollection_childTypeMap.put(biDirChildren,collectionEntityType);
//        }


    }

    default void addChild(DirChild newChild, Class<? extends Annotation> childEntityAnnotationClass,Class<? extends Annotation> childEntityCollectionAnnotationClass) throws UnknownChildTypeException {
        AtomicBoolean addedChild = new AtomicBoolean(false);
        //add to matching child collections
        for (Map.Entry<Collection<DirChild>, Class<DirChild>> entry : this.<DirChild>findAllChildCollections(childEntityAnnotationClass).entrySet()) {
            Class<? extends DirChild> targetClass = entry.getValue();
            if (newChild.getClass().equals(targetClass)) {
                (entry.getKey()).add(newChild);
                addedChild.set(true);
            }
        }
        //set matching child
        EntityReflectionUtils.doWithAnnotatedFieldsOfType(newChild.getClass(),childEntityAnnotationClass,getClass(),childField -> {
            DirChild oldChild = (DirChild) childField.get(this);
            if (oldChild != null) {
                log.warn("Overriding old child: " + oldChild + " with new Child " + newChild + " of parent " + this);
            }
            childField.set(this, newChild);
            addedChild.set(true);
        });
        if (!addedChild.get()) {
            throw new UnknownChildTypeException(getClass(), newChild.getClass());
        }

//       Field[] entityFields = findChildrenEntityFields();
//       for(Field childField: entityFields) {
//           if (childField.getType().equals(newChild.getClass())) {
//               childField.setAccessible(true);
//               BiDirChild oldChild = (BiDirChild) childField.get(this);
//               if (oldChild != null) {
//                    log.warn("Overriding old child: "+oldChild+" with new Child "+newChild+" of parent "+this);
//               }
//               childField.set(this,newChild);
//               addedChild.set(true);
//           }
//       }
    }
}
