package com.github.vincemann.springrapid.entityrelationship.dto.parent;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.IdResolvingDtoPostProcessor;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildId;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.util.EntityReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Represents a Dto, that has n Child Entities.
 * Each Child/ ChildCollection is represented by an id Field, annotated with {@link BiDirChildId} / {@link BiDirChildIdCollection}.
 * <p>
 * This Dto can be mapped to its Entity by using {@link IdResolvingDtoPostProcessor}
 */
public interface BiDirParentDto extends DirParentDto{

    Logger log = LoggerFactory.getLogger(BiDirParentDto.class);


    default <ChildId extends Serializable> ChildId findBiDirChildId(Class<? extends BiDirChild> childClazz) throws UnknownChildTypeException {
        return findChildId(childClazz,BiDirChildId.class);
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

    default Map<Class<BiDirChild>, Serializable> findAllBiDirChildIds() throws IllegalAccessException {
        return findAllChildIds(BiDirChildId.class);
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

    default <ChildId extends Serializable> Collection<ChildId> findBiDirChildrenIdCollection(Class<? extends BiDirChild> childClazz) throws IllegalAccessException {
        return findChildIdCollection(childClazz,BiDirChildIdCollection.class);
//        Field[] childrenIdCollectionFields = findBiDirChildrenIdCollectionFields();
//        for (Field field : childrenIdCollectionFields) {
//            if (field.getAnnotation(BiDirChildIdCollection.class).value().equals(childClazz)) {
//                field.setAccessible(true);
//                return (Collection<ChildId>) field.get(this);
//            }
//        }
//        throw new UnknownChildTypeException(this.getClass(), childClazz);
    }

    default Map<Class<BiDirChild>, Collection<Serializable>> findAllBiDirChildIdCollections() {
        return findAllChildIdCollections(BiDirChildIdCollection.class);
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




    /**
     * Adds childs id to {@link BiDirChildIdCollection} or {@link BiDirChildId}, depending on type it belongs to
     *
     * @param child
     */
    default void addBiDirChildsId(BiDirChild child) {
        Serializable biDirChildId = ((IdentifiableEntity) child).getId();
        if (biDirChildId == null) {
            throw new IllegalArgumentException("Id from Child must not be null");
        }
        Map<Class<BiDirChild>, Collection<Serializable>> allChildrenIdCollections = findAllBiDirChildIdCollections();
        //child collections
        for (Map.Entry<Class<BiDirChild>, Collection<Serializable>> childrenIdCollectionEntry : allChildrenIdCollections.entrySet()) {
            if (childrenIdCollectionEntry.getKey().equals(child.getClass())) {
                //need to add
                Collection<Serializable> idCollection = childrenIdCollectionEntry.getValue();
                //biDirChild is always an Identifiable Child
                idCollection.add(biDirChildId);
            }
        }
        EntityReflectionUtils.doWithAnnotatedFields(BiDirChildId.class,getClass(),field -> {
            Class<? extends BiDirChild> clazzBelongingToId = field.getAnnotation(BiDirChildId.class).value();
            if (clazzBelongingToId.equals(child.getClass())) {
                Object prevChild = field.get(this);
                if (prevChild != null) {
                    log.warn("Warning: previous Child was not null -> overriding child:  " + prevChild + " from this parent: " + this + " with new value: " + child);
                }
                field.set(this, biDirChildId);
            }
        });
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
