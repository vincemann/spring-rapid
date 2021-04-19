package com.github.vincemann.springrapid.entityrelationship.dto.child;

import com.github.vincemann.springrapid.entityrelationship.controller.dtomapper.IdResolvingDtoPostProcessor;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentIdCollection;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Represents a Dto, that has n Parent Entities.
 * Each parent is represented by an id Field, annotated with {@link BiDirParentId}.
 * <p>
 * This Dto can be mapped to its Entity by using {@link IdResolvingDtoPostProcessor}
 */
public interface BiDirChildDto extends DirChildDto {
    Logger log = LoggerFactory.getLogger(BiDirChildDto.class);

    default <ParentId extends Serializable> ParentId findBiDirParentId(Class<? extends BiDirParent> parentClazz) throws UnknownParentTypeException {
        return findParentId(parentClazz,BiDirParentId.class);
//        Field[] parentIdFields = findBiDirParentIdFields();
//        for (Field field : parentIdFields) {
//            if (field.getAnnotation(BiDirParentId.class).value().equals(parentClazz)) {
//                field.setAccessible(true);
//                return (ParentId) field.get(this);
//            }
//        }
//        throw new UnknownParentTypeException(this.getClass(), parentClazz);
    }

    default Map<Class<BiDirParent>, Serializable> findAllBiDirParentIds() {
        return findAllParentIds(BiDirParentId.class);

//        Map<Class, Serializable> parentIds = new HashMap<>();
//        Field[] parentIdFields = findBiDirParentIdFields();
//        for (Field field : parentIdFields) {
//            field.setAccessible(true);
//            Serializable id = (Serializable) field.get(this);
//            if (id != null) {
//                parentIds.put(field.getAnnotation(BiDirParentId.class).value(), id);
//            } else {
//                log.warn("Null ParentId found in BiDirDtoChild: " + this + " with idFieldName " + field.getName());
//            }
//        }
//        return parentIds;
    }

    default Map<Class<BiDirParent>, Collection<Serializable>> findAllBiDirParentIdCollections() {
        return findAllParentIdCollections(BiDirParentIdCollection.class);
    }

    default void addBiDirParentsId(BiDirParent biDirParent) {
        addParentsId(biDirParent,BiDirParentId.class,BiDirParentIdCollection.class);

//        Serializable parentId = ((IdentifiableEntity) biDirParent).getId();
//        if (parentId == null) {
//            throw new IllegalArgumentException("ParentId must not be null");
//        }
//        for (Field parentIdField : findBiDirParentIdFields()) {
//            if (parentIdField.getAnnotation(BiDirParentId.class).value().equals(biDirParent.getClass())) {
//                parentIdField.setAccessible(true);
//                Object prevParentId = parentIdField.get(this);
//                if (prevParentId != null) {
//                    log.warn("Overriding previous parentId field. OldValue: " + prevParentId);
//                }
//                parentIdField.set(this, parentId);
//            }
//        }
    }



//    default Field[] findBiDirParentIdFields() {
//        Field[] parentIdFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(getClass(), BiDirParentId.class);
//        return parentIdFields;
//    }
}
