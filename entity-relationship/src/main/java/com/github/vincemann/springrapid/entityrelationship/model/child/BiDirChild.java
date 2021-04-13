package com.github.vincemann.springrapid.entityrelationship.model.child;

import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Represents a Child of a Bidirectional relation ship (i.e. Entity with @ManyToOne typically would implement this interface).
 * The Parent of the Relation ship should implement {@link BiDirParent} and annotate its ChildCollections (containing Entities of this Type) with {@link BiDirChildCollection}
 * or its single Children (bidirectional @OneToOne) with {@link BiDirChildEntity}.
 */
public interface BiDirChild extends DirChild {
    Logger log = LoggerFactory.getLogger(BiDirChild.class);
//    Map<Class,Field[]> biDirParentFieldsCache = new HashMap<>();

    /**
     *
     * @param parentToSet
     * @throws UnknownParentTypeException   when supplied Parent does not match any of the fields in child class anntoated with {@link BiDirParentEntity}
     */
    public default void linkBiDirParent(BiDirParent parentToSet) throws UnknownParentTypeException {
       linkParent(parentToSet,BiDirParentEntity.class);
    }

    /**
     * Adds this child to its parents
     */
    public default void linkToBiDirParents(){
        Collection<BiDirParent> parents = findBiDirParents();
        for(BiDirParent parent: parents){
            if(parent!=null) {
                parent.linkBiDirChild(this);
            }else {
                log.warn("found null parent of biDirChild with type: "+ getClass().getSimpleName());
            }
        }
    }

    /**
     * Find Parent of this child, annotated with {@link BiDirParentEntity} and has same type as parentToSet.
     * Dont override, only set parentToSet, if matching parentField value was null.
     * @param parentToSet
     * @return  true, if parent was null and set
     */
    public default boolean linkBiDirParentIfNonePresent(BiDirParent parentToSet)  {
       return linkParentNotSet(parentToSet,BiDirParentEntity.class);
    }




    /**
     *
     * @return  all parent of this, that are not null
     */
    public default Collection<BiDirParent> findBiDirParents() {
        return findParents(BiDirParentEntity.class);
    }


    public default void unlinkBiDirParents() throws UnknownChildTypeException, UnknownParentTypeException{
        for(BiDirParent parent: findBiDirParents()){
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
    public default void unlinkBiDirParent(BiDirParent parentToDelete) throws UnknownParentTypeException {
        unlinkParent(parentToDelete,BiDirParentEntity.class);
    }
}
