package com.github.vincemann.springrapid.entityrelationship.model.child;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownEntityTypeException;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.BiDirEntity;
import com.github.vincemann.springrapid.entityrelationship.model.DirEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentCollection;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a Child of a Bidirectional relation ship (i.e. Entity with @ManyToOne typically would implement this interface).
 * The Parent of the Relation ship should implement {@link BiDirParent} and annotate its ChildCollections (containing Entities of this Type) with {@link BiDirChildCollection}
 * or its single Children (bidirectional @OneToOne) with {@link BiDirChildEntity}.
 */
public interface BiDirChild extends BiDirEntity {
    // FIND

    /**
     * Find the BiDirParent Collections (all fields of this parent annotated with {@link BiDirParentCollection} )
     * mapped to the Type of the Entities in the Collection.
     * @return
     */
    default Map<Collection<BiDirParent>,Class<BiDirParent>> findBiDirParentCollections(){
        return findEntityCollections(BiDirParentCollection.class);
    }
    /**
     *
     * @return  all parent of this, that are not null
     */
    default Collection<BiDirParent> findSingleBiDirParents() {
        return findSingleEntities(BiDirParentEntity.class);
    }




    // LINK / UNLINK

    /**
     *
     * @param parentToSet
     * @throws UnknownParentTypeException   when supplied Parent does not match any of the fields in child class anntoated with {@link BiDirParentEntity}
     */
    default void linkBiDirParent(BiDirParent parentToSet) throws UnknownParentTypeException {
       linkEntity((IdentifiableEntity) parentToSet,BiDirParentEntity.class,BiDirParentCollection.class);
    }

    default void unlinkBiDirParents() throws UnknownChildTypeException, UnknownParentTypeException{
        for(BiDirParent parent: findSingleBiDirParents()){
            if(parent!=null) {
                this.unlinkBiDirParent(parent);
            }else {
                log.warn("Parent Reference of BiDirChild with type: "+getClass().getSimpleName()+" was not set when deleting -> parent was deleted before child");
            }
        }
    }

    /**
     * This Child wont know about parentToDelete after this operation.
     * Set all {@link BiDirParentEntity}s of this {@link BiDirChild} to null.
     * @param parentToDelete
     * @throws UnknownParentTypeException   thrown, if parentToDelete is of unknown type -> no field , annotated as {@link BiDirParentEntity}, with the most specific type of parentToDelete, exists in Child (this).
     */
    default void unlinkBiDirParent(BiDirParent parentToDelete) throws UnknownParentTypeException {
        unlinkEntity((IdentifiableEntity) parentToDelete,BiDirParentEntity.class,BiDirParentCollection.class);
    }

    /**
     * All children {@link BiDirChild} of this parent wont know about this parent, after this operation.
     * Clear all {@link BiDirChildCollection}s of this parent.
     * Call this, before you want to delete this parent.
     * @throws UnknownParentTypeException
     */
    default void unlinkParentsChildren() throws UnknownEntityTypeException {
        for(BiDirParent parent: findSingleBiDirParents()){
            parent.unlinkBiDirChild(this);
        }
        for(Map.Entry<Collection<BiDirParent>,Class<BiDirParent>> entry: findBiDirParentCollections().entrySet()){
            Collection<BiDirParent> parentCollection = entry.getKey();
            for(BiDirParent parent: parentCollection){
                parent.unlinkBiDirChild(this);
            }
            parentCollection.clear();
        }
    }

}
