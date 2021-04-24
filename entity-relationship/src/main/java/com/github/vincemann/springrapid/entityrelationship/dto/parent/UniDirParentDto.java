package com.github.vincemann.springrapid.entityrelationship.dto.parent;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.dto.DirDto;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.UniDirChildId;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.UniDirChildIdCollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * See {@link BiDirParentDto}
 */
public interface UniDirParentDto extends DirDto {
    Logger log = LoggerFactory.getLogger(UniDirParentDto.class);


//    default <ChildId extends Serializable> ChildId findUniDirChildId(Class<? extends UniDirChild> childClazz) throws UnknownChildTypeException {
//        return findChildId(childClazz,UniDirChildId.class);
//    }

    default Map<Class<IdentifiableEntity>, Serializable> findUniDirChildIds(){
        return findEntityIds(UniDirChildId.class);
    }



//    default <ChildId extends Serializable> Collection<ChildId> findUniDirChildIdCollection(Class<? extends UniDirChild> childClazz) {
//        return findChildIdCollection(childClazz,UniDirChildIdCollection.class);
//    }



    default Map<Class<IdentifiableEntity>, Collection<Serializable>> findUniDirChildIdCollections(){
        return findEntityIdCollections(UniDirChildIdCollection.class);
    }

    /**
     * Adds child's id to {@link UniDirChildIdCollection} or {@link UniDirChildId}, depending on entity type it belongs to
     *
     * @param child
     */
    default void addUniDirChildId(IdentifiableEntity child) {
        addEntityId(child, UniDirChildId.class, UniDirChildIdCollection.class);
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
