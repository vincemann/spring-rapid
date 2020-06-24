package com.github.vincemann.springrapid.entityrelationship.model.child;

import com.github.vincemann.springrapid.entityrelationship.exception.UnknownChildTypeException;
import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.BiDirEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a Child of a Bidirectional relation ship (i.e. Entity with @ManyToOne typically would implement this interface).
 * The Parent of the Relation ship should implement {@link BiDirParent} and annotate its ChildCollections (containing Entities of this Type) with {@link BiDirChildCollection}
 * or its single Children (bidirectional @OneToOne) with {@link BiDirChildEntity}.
 */
public interface BiDirChild extends BiDirEntity, DirChild {
    Logger log = LoggerFactory.getLogger(BiDirChild.class);
//    Map<Class,Field[]> biDirParentFieldsCache = new HashMap<>();

    /**
     *
     * @param parentToSet
     * @throws UnknownParentTypeException   when supplied Parent does not match any of the fields in child class anntoated with {@link BiDirParentEntity}
     * @throws IllegalAccessException
     */
    public default void addBiDirParent(BiDirParent parentToSet) throws UnknownParentTypeException {
       addParent(parentToSet,BiDirParentEntity.class);
//        AtomicBoolean parentSet = new AtomicBoolean(false);
//        for(Field parentField: findParentFields()){
//            if(parentToSet.getClass().equals(parentField.getType())){
//                parentField.setAccessible(true);
//                parentField.set(this,parentToSet);
//                parentSet.set(true);
//            }
//        }
//        if(!parentSet.get()){
//            throw new UnknownParentTypeException(this.getClass(),parentToSet.getClass());
//        }
    }

    /**
     * Adds this child to its parents
     * @throws IllegalAccessException
     */
    public default void addToBiDirParents() throws IllegalAccessException {
        Collection<BiDirParent> parents = findBiDirParents();
        for(BiDirParent parent: parents){
            if(parent!=null) {
                parent.addBiDirChild(this);
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
    public default boolean addBiDirParentIfNull(BiDirParent parentToSet)  {
       return addParentIfNull(parentToSet,BiDirParentEntity.class);
//        for(Field parentField: findParentFields()){
//            if(parentToSet.getClass().equals(parentField.getType())){
//                parentField.setAccessible(true);
//                if(parentField.get(this)==null) {
//                    parentField.set(this, parentToSet);
//                }
//            }
//        }
//        return parentSet.get();
    }

//    /**
//     * Find all fields of this child, annotated with {@link BiDirParentEntity}
//     * @return
//     */
//    public default Field[] findParentFields(){
//        Field[] parentFieldsFromCache = biDirParentFieldsCache.get(this.getClass());
//        if(parentFieldsFromCache==null){
//            Field[] parentFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(getClass(), BiDirParentEntity.class);
//            biDirParentFieldsCache.put(this.getClass(),parentFields);
//            return parentFields;
//        }else {
//            return parentFieldsFromCache;
//        }
//    }


    /**
     *
     * @return  all parent of this, that are not null
     * @throws IllegalAccessException
     */
    public default Collection<BiDirParent> findBiDirParents() {
        return findParents(BiDirParentEntity.class);
//        Collection<BiDirParent> result = new ArrayList<>();
//        Field[] parentFields = findParentFields();
//        for(Field parentField: parentFields) {
//            parentField.setAccessible(true);
//            BiDirParent biDirParent = (BiDirParent) parentField.get(this);
//            if(biDirParent!=null) {
//                result.add(biDirParent);
//            }
//        }
//        return result;
    }


    public default void dismissBiDirParents() throws UnknownChildTypeException, UnknownParentTypeException{
        for(BiDirParent parent: findBiDirParents()){
            if(parent!=null) {
                this.dismissBiDirParent(parent);
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
    public default void dismissBiDirParent(BiDirParent parentToDelete) throws UnknownParentTypeException {
        dismissParent(parentToDelete,BiDirParentEntity.class);
//        AtomicBoolean parentRemoved = new AtomicBoolean(false);
//        Field[] parentFields = findParentFields();
//        for(Field parentField: parentFields){
//            parentField.setAccessible(true);
//            BiDirParent  parent = (BiDirParent) parentField.get(this);
//            if(parent!=null) {
//                if (parentToDelete.getClass().equals(parent.getClass())) {
//                    parentField.set(this,null);
//                    parentRemoved.set(true);
//                }
//            }
//        }
//        if(!parentRemoved.get()){
//            throw new UnknownParentTypeException(this.getClass(),parentToDelete.getClass());
//        }
    }
}
