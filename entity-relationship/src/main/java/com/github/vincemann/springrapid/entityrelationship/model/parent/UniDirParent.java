package com.github.vincemann.springrapid.entityrelationship.model.parent;

import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.DirEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.UniDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.UniDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.UniDirChildEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Same as {@link BiDirParent} but for a unidirectional relationship.
 */
public interface UniDirParent extends DirEntity {

    // FIND


    /**
     * Find the UniDirChildren Collections (all fields of this parent annotated with {@link UniDirChildCollection} and not null )
     * and the Type of the Entities in the Collection.
     *
     * @return
     */
    default Map<Collection<UniDirChild>, Class<UniDirChild>> findUniDirChildCollections()  {
        return findEntityCollections(UniDirChildCollection.class);
    }
    /**
     * Find the single UniDirChildren (all fields of this parent annotated with {@link UniDirChildEntity} and not null.
     * @return
     */
    default Set<UniDirChild> findSingleUniDirChildren() {
        return findSingleEntities(UniDirChildEntity.class);
    }




    // LINK / UNLINK


    /**
     * Add a new Child to this parent.
     * Call this, when saving a {@link UniDirChild} of this parent.
     * child will be added to fields with {@link UniDirChildCollection} and fields with {@link UniDirChildEntity} will be set with newChild, when most specific type matches of newChild matches the field.
     * Child wont be added and UnknownChildTypeException will be thrown when corresponding {@link UniDirChildCollection} is null.
     *
     * @param newChild
     * @throws UnknownChildTypeException
     */
    default void linkUniDirChild(UniDirChild newChild) throws UnknownChildTypeException{
        linkEntity(newChild,UniDirChildEntity.class,UniDirChildCollection.class);
    }

    /**
     * This parent wont know about the given uniDirChildToRemove after this operation.
     * Call this, before you delete the uniDirChildToRemove.
     * Case 1: Remove Child {@link UniDirChild} from all {@link UniDirChildCollection}s from this parent.
     * Case 2: Set {@link UniDirChildEntity}Field to null if child is not saved in a collection in this parent.
     *
     * @param toRemove
     * @throws UnknownChildTypeException
     */
    default void unlinkUniDirChild(UniDirChild toRemove) throws UnknownChildTypeException{
        unlinkEntity(toRemove,UniDirChildEntity.class,UniDirChildCollection.class);
    }



}
