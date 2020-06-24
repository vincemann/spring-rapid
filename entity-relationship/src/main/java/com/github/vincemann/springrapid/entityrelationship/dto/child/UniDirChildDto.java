package com.github.vincemann.springrapid.entityrelationship.dto.child;

import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.UniDirParentId;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * See {@link BiDirChildDto}
 */
public interface UniDirChildDto extends DirChildDto {
    Logger log = LoggerFactory.getLogger(UniDirChildDto.class);

    default <ParentId extends Serializable> ParentId findUniDirParentId(Class<? extends UniDirParent> parentClazz) throws UnknownParentTypeException, IllegalAccessException {
        return findParentId(parentClazz, UniDirParentId.class);

//        Field[] parentIdFields = findUniDirParentIdFields();
//        for (Field field : parentIdFields) {
//            if (field.getAnnotation(UniDirParentId.class).value().equals(parentClazz)) {
//                field.setAccessible(true);
//                return (ParentId) field.get(this);
//            }
//        }
//        throw new UnknownParentTypeException(this.getClass(), parentClazz);
    }

    default Map<Class<UniDirParent>, Serializable> findAllUniDirParentIds() throws IllegalAccessException {
        return findAllParentIds(UniDirParentId.class);


//        Map<Class, Serializable> parentIds = new HashMap<>();
//        Field[] parentIdFields = findUniDirParentIdFields();
//        for (Field field : parentIdFields) {
//            field.setAccessible(true);
//            Serializable id = (Serializable) field.get(this);
//            if (id != null) {
//                parentIds.put(field.getAnnotation(UniDirParentId.class).value(), id);
//            } else {
//                log.warn("Warning: Null id found in BiDirDtoChild " + this + " for ParentIdField with name: " + field.getName());
//            }
//        }
//        return parentIds;
    }

    default void addUniDirParentsId(UniDirParent uniDirParent) throws IllegalAccessException {
        addParentsId(uniDirParent,UniDirParentId.class);
//        for (Field parentIdField : findUniDirParentIdFields()) {
//            if (parentIdField.getAnnotation(UniDirParentId.class).value().equals(uniDirParent.getClass())) {
//                ReflectionUtils.makeAccessible(parentIdField);
//                Object prevParentId = parentIdField.get(this);
//                if (prevParentId != null) {
//                    log.warn("Warning, prev ParentId: " + prevParentId + " was not null -> overriding with new value: " + parentId);
//                }
//                parentIdField.set(this, parentId);
//            }
//        }
    }

//    default Field[] findUniDirParentIdFields() {
//        Field[] parentIdFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(getClass(), UniDirParentId.class);
//        return parentIdFields;
//    }
}
