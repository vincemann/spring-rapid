package com.github.vincemann.springrapid.entityrelationship.dto.uniDir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.uniDir.child.UniDirChild;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * See {@link com.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirParentDto}
 */
public interface UniDirParentDto {
    Logger log = LoggerFactory.getLogger(UniDirParentDto.class);


    default <ChildId extends Serializable> ChildId findUniDirChildId(Class<? extends UniDirChild> childClazz) throws UnknownChildTypeException, IllegalAccessException {
        AtomicReference<ChildId> result = new AtomicReference<>();
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            if (field.getAnnotation(UniDirChildId.class).value().equals(childClazz)) {
                ReflectionUtils.makeAccessible(field);
                if (result.get()!=null){
                    throw new IllegalArgumentException("There cant be two members with directional annotation type value");
                }
                result.set((ChildId) field.get(this));
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(UniDirChildId.class));
        if (result.get()==null){
            throw new UnknownChildTypeException(this.getClass(), childClazz);
        }else {
            return result.get();
        }

        //        Field[] childrenIdFields = findUniDirChildrenIdFields();
//
//        for (Field field : childrenIdFields) {
//            if (field.getAnnotation(UniDirChildId.class).value().equals(childClazz)) {
//                field.setAccessible(true);
//                return (ChildId) field.get(this);
//            }
//        }
    }



    default <ChildId extends Serializable> Collection<ChildId> findUniDirChildrenIdCollection(Class<? extends UniDirChild> childClazz) throws IllegalAccessException {
        AtomicReference<Collection<ChildId>> result = new AtomicReference<>();
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            if (field.getAnnotation(UniDirChildIdCollection.class).value().equals(childClazz)) {
                ReflectionUtils.makeAccessible(field);
                if (result.get()!=null){
                    throw new IllegalArgumentException("There cant be two members with directional annotation type value");
                }
                result.set((Collection<ChildId>) field.get(this));
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(UniDirChildIdCollection.class));
        if (result.get()==null){
            throw new UnknownChildTypeException(this.getClass(), childClazz);
        }else {
            return result.get();
        }
        //        Field[] childrenIdCollectionFields = findUniDirChildrenIdCollectionFields();
//        for (Field field : childrenIdCollectionFields) {
//            if (field.getAnnotation(UniDirChildIdCollection.class).value().equals(childClazz)) {
//                field.setAccessible(true);
//                return (Collection<ChildId>) field.get(this);
//            }
//        }
//        throw new UnknownChildTypeException(this.getClass(), childClazz);
    }

    default Map<Class, Serializable> findTypeUniDirChildIdMap() throws IllegalAccessException {
        final Map<Class, Serializable> result = new HashMap<>();
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            ReflectionUtils.makeAccessible(field);
            Serializable id = (Serializable) field.get(this);
            if (id != null) {
                result.put(field.getAnnotation(UniDirChildId.class).value(), id);
            } else {
                log.warn("Null id found in UniDirParentDto " + this + " for ChildIdField with name: " + field.getName());
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(UniDirChildId.class));
        return result;
//        Field[] childIdFields = findUniDirChildrenIdFields();
//        for (Field field : childIdFields) {
//            field.setAccessible(true);
//            Serializable id = (Serializable) field.get(this);
//            if (id != null) {
//                childrenIds.put(field.getAnnotation(UniDirChildId.class).value(), id);
//            } else {
//                log.warn("Null id found in UniDirParentDto " + this + " for ChildIdField with name: " + field.getName());
//            }
//        }
//        return childrenIds;
    }

    default Map<Class, Collection<Serializable>> findTypeUniDirChildrenIdCollectionMap() throws IllegalAccessException {
        final Map<Class, Collection<Serializable>> result = new HashMap<>();
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            ReflectionUtils.makeAccessible(field);
            Collection<Serializable> idCollection = (Collection<Serializable>) field.get(this);
            if (idCollection != null) {
                result.put(field.getAnnotation(UniDirChildIdCollection.class).value(), idCollection);
            }/*else {
               throw new IllegalArgumentException("Null idCollection found in UniDirParentDto "+ this + " for ChildIdCollectionField with name: " + field.getName());
            }*/
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(UniDirChildIdCollection.class));

        return result;
//        Map<Class, Collection<Serializable>> childrenIdCollections = new HashMap<>();
//        Field[] childIdCollectionFields = findUniDirChildrenIdCollectionFields();
//        for (Field field : childIdCollectionFields) {
//            field.setAccessible(true);
//            Collection<Serializable> idCollection = (Collection<Serializable>) field.get(this);
//            if (idCollection != null) {
//                childrenIdCollections.put(field.getAnnotation(UniDirChildIdCollection.class).value(), idCollection);
//            }/*else {
//               throw new IllegalArgumentException("Null idCollection found in UniDirParentDto "+ this + " for ChildIdCollectionField with name: " + field.getName());
//            }*/
//        }
    }

    /**
     * Adds child's id to {@link UniDirChildIdCollection} or {@link UniDirChildId}, depending on entity type it belongs to
     *
     * @param child
     */
    default void addUniDirChildsId(IdentifiableEntity child) throws IllegalAccessException {
        Serializable biDirChildId = child.getId();
        if (biDirChildId == null) {
            throw new IllegalArgumentException("Id from Child must not be null");
        }
        Map<Class, Collection<Serializable>> allChildrenIdCollections = findTypeUniDirChildrenIdCollectionMap();
        //child collections
        for (Map.Entry<Class, Collection<Serializable>> childrenIdCollectionEntry : allChildrenIdCollections.entrySet()) {
            if (childrenIdCollectionEntry.getKey().equals(child.getClass())) {
                //need to add
                Collection<Serializable> idCollection = childrenIdCollectionEntry.getValue();
                //biDirChild is always an Identifiable Child
                idCollection.add(biDirChildId);
            }
        }
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            ReflectionUtils.makeAccessible(field);
            Class<? extends UniDirChild> clazzBelongingToId = field.getAnnotation(UniDirChildId.class).value();
            if (clazzBelongingToId.equals(child.getClass())) {
                Object prevChild = field.get(this);
                if (prevChild != null) {
                    log.warn("Warning: previous Child was not null -> overriding child:  " + prevChild + " from this parent: " + this + " with new value: " + child);
                }
                field.set(this, biDirChildId);
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(UniDirChildId.class));
//        //single children
//        Field[] childrenIdFields = findUniDirChildrenIdFields();
//        for (Field field : childrenIdFields) {
//            field.setAccessible(true);
//            Class<? extends UniDirChild> clazzBelongingToId = field.getAnnotation(UniDirChildId.class).value();
//            if (clazzBelongingToId.equals(child.getClass())) {
//                Object prevChild = field.get(this);
//                if (prevChild != null) {
//                    log.warn("Warning: previous Child was not null -> overriding child:  " + prevChild + " from this parent: " + this + " with new value: " + child);
//                }
//                field.set(this, biDirChildId);
//            }
//        }
    }

//    default Field[] findUniDirChildrenIdCollectionFields() {
//        Field[] childrenIdCollectionFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(this.getClass(), UniDirChildIdCollection.class);
//        return childrenIdCollectionFields;
//    }
//
//    default Field[] findUniDirChildrenIdFields() {
//        Field[] childrenIdFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(getClass(), UniDirChildId.class);
//        return childrenIdFields;
//    }
}
