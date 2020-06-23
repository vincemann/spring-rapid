package com.github.vincemann.springrapid.entityrelationship.dto.biDir;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.IdResolvingDtoPostProcessor;
import com.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirChildId;
import com.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.biDir.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.uniDir.child.UniDirChild;
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
 * Represents a Dto, that has n Child Entities.
 * Each Child/ ChildCollection is represented by an id Field, annotated with {@link BiDirChildId} / {@link BiDirChildIdCollection}.
 * <p>
 * This Dto can be mapped to its Entity by using {@link IdResolvingDtoPostProcessor}
 */
public interface BiDirParentDto {

    Logger log = LoggerFactory.getLogger(BiDirParentDto.class);


    default <ChildId extends Serializable> ChildId findBiDirChildId(Class<? extends BiDirChild> childClazz) throws UnknownChildTypeException, IllegalAccessException {
        AtomicReference<ChildId> result = new AtomicReference<>();
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            if (field.getAnnotation(BiDirChildId.class).value().equals(childClazz)) {
                ReflectionUtils.makeAccessible(field);
                if (result.get()!=null){
                    throw new IllegalArgumentException("There cant be two members with directional annotation type value");
                }
                result.set((ChildId) field.get(this));
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(BiDirChildId.class));
        if (result.get()==null){
            throw new UnknownChildTypeException(this.getClass(), childClazz);
        }else {
            return result.get();
        }
//
//        Field[] childrenIdFields = findBiDirChildrenIdFields();
//        for (Field field : childrenIdFields) {
//            if (field.getAnnotation(BiDirChildId.class).value().equals(childClazz)) {
//                field.setAccessible(true);
//                return (ChildId) field.get(this);
//            }
//        }
//        throw new UnknownChildTypeException(this.getClass(), childClazz);
    }

    default Map<Class, Serializable> findTypeBiDirIdMap() throws IllegalAccessException {
        final Map<Class, Serializable> result = new HashMap<>();
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            ReflectionUtils.makeAccessible(field);
            Serializable id = (Serializable) field.get(this);
            if (id != null) {
                result.put(field.getAnnotation(BiDirChildId.class).value(), id);
            } else {
                log.warn("Null id found in BiDirParentDto " + this + " for ChildIdField with name: " + field.getName());
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(BiDirChildId.class));
        return result;
//        Map<Class, Serializable> childrenIds = new HashMap<>();
//        Field[] childIdFields = findBiDirChildrenIdFields();
//        for (Field field : childIdFields) {
//            field.setAccessible(true);
//            Serializable id = (Serializable) field.get(this);
//            if (id != null) {
//                childrenIds.put(field.getAnnotation(BiDirChildId.class).value(), id);
//            } else {
//                log.warn("Warning: Null id found in BiDirDtoParent " + this + " for ChildIdField with name: " + field.getName());
//            }
//        }
//        return childrenIds;
    }

    default Map<Class, Collection<Serializable>> findTypeBiDirChildrenIdCollectionMap() throws IllegalAccessException {
        final Map<Class, Collection<Serializable>> result = new HashMap<>();
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            ReflectionUtils.makeAccessible(field);
            Collection<Serializable> idCollection = (Collection<Serializable>) field.get(this);
            if (idCollection != null) {
                result.put(field.getAnnotation(BiDirChildIdCollection.class).value(), idCollection);
            }/*else {
               throw new IllegalArgumentException("Null idCollection found in UniDirParentDto "+ this + " for ChildIdCollectionField with name: " + field.getName());
            }*/
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(BiDirChildIdCollection.class));

        return result;
//        Map<Class, Collection<Serializable>> childrenIdCollections = new HashMap<>();
//        Field[] childIdCollectionFields = findBiDirChildrenIdCollectionFields();
//        for (Field field : childIdCollectionFields) {
//            field.setAccessible(true);
//            Collection<Serializable> idCollection = (Collection<Serializable>) field.get(this);
//            if (idCollection != null) {
//                childrenIdCollections.put(field.getAnnotation(BiDirChildIdCollection.class).value(), idCollection);
//            }/*else {
//                throw new IllegalArgumentException("Null idCollection found in BiDirDtoParent "+ this + " for ChildIdCollectionField with name: " + field.getName());
//            }*/
//        }
//        return childrenIdCollections;
    }


    default <ChildId extends Serializable> Collection<ChildId> findBiDirChildrenIdCollection(Class<? extends BiDirChild> childClazz) throws IllegalAccessException {
        AtomicReference<Collection<ChildId>> result = new AtomicReference<>();
        ReflectionUtils.doWithFields(this.getClass(),field -> {
            if (field.getAnnotation(BiDirChildIdCollection.class).value().equals(childClazz)) {
                ReflectionUtils.makeAccessible(field);
                if (result.get()!=null){
                    throw new IllegalArgumentException("There cant be two members with directional annotation type value");
                }
                result.set((Collection<ChildId>) field.get(this));
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(BiDirChildIdCollection.class));
        if (result.get()==null){
            throw new UnknownChildTypeException(this.getClass(), childClazz);
        }else {
            return result.get();
        }
//        Field[] childrenIdCollectionFields = findBiDirChildrenIdCollectionFields();
//        for (Field field : childrenIdCollectionFields) {
//            if (field.getAnnotation(BiDirChildIdCollection.class).value().equals(childClazz)) {
//                field.setAccessible(true);
//                return (Collection<ChildId>) field.get(this);
//            }
//        }
//        throw new UnknownChildTypeException(this.getClass(), childClazz);
    }

    /**
     * Adds childs id to {@link BiDirChildIdCollection} or {@link BiDirChildId}, depending on type it belongs to
     *
     * @param child
     */
    default void addBiDirChildsId(BiDirChild child) throws IllegalAccessException {
        Serializable biDirChildId = ((IdentifiableEntity) child).getId();
        if (biDirChildId == null) {
            throw new IllegalArgumentException("Id from Child must not be null");
        }
        Map<Class, Collection<Serializable>> allChildrenIdCollections = findTypeBiDirChildrenIdCollectionMap();
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
            Class<? extends BiDirChild> clazzBelongingToId = field.getAnnotation(BiDirChildId.class).value();
//            Class<? extends BiDirChildId> clazzBelongingToId =
            if (clazzBelongingToId.equals(child.getClass())) {
                Object prevChild = field.get(this);
                if (prevChild != null) {
                    log.warn("Warning: previous Child was not null -> overriding child:  " + prevChild + " from this parent: " + this + " with new value: " + child);
                }
                field.set(this, biDirChildId);
            }
        },new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(BiDirChildId.class));
//        Serializable biDirChildId = ((IdentifiableEntity) child).getId();
//        if (biDirChildId == null) {
//            throw new IllegalArgumentException("Id from Child must not be null");
//        }
//
//
//        Map<Class, Collection<Serializable>> allChildrenIdCollections = findTypeBiDirChildrenIdCollectionMap();
//        //child collections
//        for (Map.Entry<Class, Collection<Serializable>> childrenIdCollectionEntry : allChildrenIdCollections.entrySet()) {
//            if (childrenIdCollectionEntry.getKey().equals(child.getClass())) {
//                //need to add
//                Collection<Serializable> idCollection = childrenIdCollectionEntry.getValue();
//                //biDirChild is always an Identifiable Child
//                idCollection.add(biDirChildId);
//            }
//        }
//        //single children
//        Field[] childrenIdFields = findBiDirChildrenIdFields();
//        for (Field field : childrenIdFields) {
//            field.setAccessible(true);
//            Class<? extends BiDirChild> clazzBelongingToId = field.getAnnotation(BiDirChildId.class).value();
//            if (clazzBelongingToId.equals(child.getClass())) {
//                Object prevChild = field.get(this);
//                if (prevChild != null) {
//                    log.warn("Warning: prevChild was not null -> overriding child:  " + prevChild + " from this parent: " + this);
//                }
//                field.set(this, biDirChildId);
//            }
//        }
    }

//    default Field[] findBiDirChildrenIdCollectionFields() {
//        Field[] childrenIdCollectionFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(this.getClass(), BiDirChildIdCollection.class);
//        return childrenIdCollectionFields;
//    }
//
//    default Field[] findBiDirChildrenIdFields() {
//        Field[] childrenIdFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(getClass(), BiDirChildId.class);
//        return childrenIdFields;
//    }
}
