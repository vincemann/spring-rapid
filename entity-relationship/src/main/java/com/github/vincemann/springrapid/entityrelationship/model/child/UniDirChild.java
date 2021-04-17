package com.github.vincemann.springrapid.entityrelationship.model.child;

import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentCollection;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.UniDirParentCollection;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.UniDirParentEntity;

import java.util.Collection;
import java.util.Map;

/**
 * Same as {@link BiDirChild} but for a unidirectional relationship.
 */
public interface UniDirChild extends DirChild {

    // FIND

    default Map<Collection<UniDirChild>,Class<UniDirChild>> findUniDirParentCollections(){
        return findEntityCollections(BiDirParentCollection.class);
    }

    /**
     * @return all parent of this, that are not null
     */
    public default Collection<UniDirParent> findSingleUniDirParents() {
        return findSingleEntities(UniDirParentEntity.class);
    }




    // LINK / UNLINK

    /**
     * @param parentToSet
     * @throws UnknownParentTypeException when supplied Parent does not match any of the fields in child class anntoated with {@link UniDirParentEntity}
     */
    public default void linkUniDirParent(UniDirParent parentToSet) throws UnknownParentTypeException {
        linkEntity(parentToSet, UniDirParentEntity.class, UniDirParentCollection.class);
    }

    /**
     * This Child wont know about parentToDelete after this operation.
     * Sets {@link UniDirParentEntity} Field, that has same Type as {@param parentToDelete}, to null.
     *
     * @param parentToDelete
     * @throws UnknownParentTypeException thrown, if parentToDelete is of unknown type -> no field , annotated as {@link UniDirParentEntity}, with the most specific type of parentToDelete, exists in Child (this).
     */
    public default void unlinkUniDirParent(UniDirParent parentToDelete) throws UnknownParentTypeException {
        unlinkEntity(parentToDelete,UniDirParentEntity.class,UniDirParentCollection.class);
    }

//    /**
//     * Find Parent of this child, annotated with {@link UniDirParentEntity} that has the same type as {@param parentToSet}.
//     * Does not override. The parent will only be set to {@param parentToSet}, if matching parentField's value is null.
//     *
//     * @param parentToSet
//     * @return true, if parent was null and is set to {@param parentToSet}, otherwise false
//     */
//    public default boolean linkUniDirParentIfNonePresent(UniDirParent parentToSet) {
//        return linkParentNotSet(parentToSet,UniDirParentEntity.class);

//    }


}
