package com.github.vincemann.springrapid.entityrelationship.model.parent;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.BiDirEntity;
import com.github.vincemann.springrapid.entityrelationship.model.DirEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Represents a parent of a bidirectional relationship (i.e. Entity with @OneToMany typically would implement this interface).
 * The Child of the relationship should implement {@link BiDirChild} and annotate its parents with {@link BiDirParentEntity}.
 */
public interface  BiDirParent extends BiDirEntity {


    // FIND

    /**
     * Find the BiDirChildren Collections (all fields of this parent annotated with {@link BiDirChildCollection} )
     * mapped to the Type of the Entities in the Collection.
     * @return
     */
    default Map<Collection<BiDirChild>,Class<BiDirChild>> findBiDirChildCollections(){
        return findEntityCollections(BiDirChildCollection.class);
    }

    /**
     * Find the single BiDirChildren (all fields of this parent annotated with {@link BiDirChildEntity} and not null.
     * @return
     */
    default Set<BiDirChild> findSingleBiDirChildren(){
        return findSingleEntities(BiDirChildEntity.class);
    }




    // LINK / UNLINK

    /**
     * Add a new Child to this parent.
     * Call this, when saving a {@link BiDirChild} of this parent.
     * child will be added to fields with {@link BiDirChildCollection} and fields with {@link BiDirChildEntity} will be set with newChild, when most specific type matches of newChild matches the field.
     * Child wont be added and UnknownChildTypeException will be thrown when corresponding {@link BiDirChildCollection} is null.
     * @param newChild
     * @throws UnknownChildTypeException
     */
   default void linkBiDirChild(BiDirChild newChild) throws UnknownChildTypeException{
       linkEntity((IdentifiableEntity) newChild, BiDirChildEntity.class, BiDirChildCollection.class);
   }

    /**
     * This parent wont know about the given biDirChildToRemove after this operation.
     * Call this, before you delete the biDirChildToRemove.
     * Case 1: Remove Child {@link BiDirChild} from all {@link BiDirChildCollection}s from this parent.
     * Case 2: Set {@link BiDirChildEntity}Field to null if child is not saved in a collection in this parent.
     * @param biDirChildToRemove
     * @throws UnknownChildTypeException
     */
    default void unlinkBiDirChild(BiDirChild biDirChildToRemove) throws UnknownChildTypeException{
        unlinkEntity((IdentifiableEntity) biDirChildToRemove,BiDirChildEntity.class,BiDirChildCollection.class);
    }

    /**
     * All children {@link BiDirChild} of this parent wont know about this parent, after this operation.
     * Clear all {@link BiDirChildCollection}s of this parent.
     * Call this, before you want to delete this parent.
     * @throws UnknownParentTypeException
     */
    default void unlinkChildrensParent() throws UnknownParentTypeException{
        for(BiDirChild child: findSingleBiDirChildren()){
            child.unlinkBiDirParent(this);
        }
        for(Map.Entry<Collection<BiDirChild>,Class<BiDirChild>> entry: findBiDirChildCollections().entrySet()){
            Collection<BiDirChild> childrenCollection = entry.getKey();
            for(BiDirChild biDirChild: childrenCollection){
                biDirChild.unlinkBiDirParent(this);
            }
            childrenCollection.clear();
        }
    }
}
