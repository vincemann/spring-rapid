package io.github.vincemann.generic.crud.lib.model.uniDir;

import io.github.vincemann.generic.crud.lib.service.exception.UnknownChildTypeException;
import io.github.vincemann.generic.crud.lib.service.exception.UnknownParentTypeException;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public interface UniDirChild extends UniDirEntity {

    Map<Class, Field[]> uniDirParentFieldsCache = new HashMap<>();

    /**
     *
     * @param parentToSet
     * @throws UnknownParentTypeException   when supplied Parent does not match any of the fields in child class anntoated with {@link UniDirParentEntity}
     * @throws IllegalAccessException
     */
    public default void findAndSetParent(Object parentToSet) throws UnknownParentTypeException, IllegalAccessException {
        AtomicBoolean parentSet = new AtomicBoolean(false);
        for(Field parentField: findParentFields()){
            if(parentToSet.getClass().equals(parentField.getType())){
                parentField.setAccessible(true);
                parentField.set(this,parentToSet);
                parentSet.set(true);
            }
        }
        if(!parentSet.get()){
            throw new UnknownParentTypeException(this.getClass(),parentToSet.getClass());
        }
    }

    /**
     * Adds this child to its parents
     * @throws IllegalAccessException
     */
    public default void addChildToParents() throws IllegalAccessException {
        Collection<UniDirParent> parents = findParents();
        for(UniDirParent parent: parents){
            if(parent!=null) {
                parent.addChild(this);
            }else {
                System.err.println("found null parent of uniDirChild with type: " +getClass().getSimpleName());
            }
        }
    }

    /**
     * Find Parent of this child, annotated with {@link UniDirParentEntity} and has same type as parentToSet.
     * Dont override, only set parentToSet, if matching parentField value was null.
     * @param parentToSet
     * @return  true, if parent was null and set
     * @throws IllegalAccessException
     */
    public default boolean findAndSetParentIfNull(UniDirParent parentToSet) throws IllegalAccessException {
        AtomicBoolean parentSet = new AtomicBoolean(false);
        for(Field parentField: findParentFields()){
            if(parentToSet.getClass().equals(parentField.getType())){
                parentField.setAccessible(true);
                if(parentField.get(this)==null) {
                    parentField.set(this, parentToSet);
                }
            }
        }
        return parentSet.get();
    }

    /**
     * Find all fields of this child, annotated with {@link UniDirParentEntity}
     * @return
     */
    public default Field[] findParentFields(){
        Field[] parentFieldsFromCache = uniDirParentFieldsCache.get(this.getClass());
        if(parentFieldsFromCache==null){
            Field[] parentFields = ReflectionUtils.getDeclaredFieldsAnnotatedWith(getClass(), UniDirParentEntity.class, true);
            uniDirParentFieldsCache.put(this.getClass(),parentFields);
            return parentFields;
        }else {
            return parentFieldsFromCache;
        }
    }


    /**
     *
     * @return  all parent of this, that are not null
     * @throws IllegalAccessException
     */
    public default Collection<UniDirParent> findParents() throws IllegalAccessException {
        Collection<UniDirParent> result = new ArrayList<>();
        Field[] parentFields = findParentFields();
        for(Field parentField: parentFields) {
            parentField.setAccessible(true);
            UniDirParent uniDirParent = (UniDirParent) parentField.get(this);
            if(uniDirParent!=null) {
                result.add(uniDirParent);
            }
        }
        return result;
    }


    public default void dismissParents() throws UnknownChildTypeException, UnknownParentTypeException, IllegalAccessException {
        for(UniDirParent parent: findParents()){
            if(parent!=null) {
                this.dismissParent(parent);
            }else {
                System.err.println("parent Reference of UniDirChild with type: " + this.getClass().getSimpleName() +" was not set when deleting -> parent was deleted before child");
            }
        }
    }

    /**
     * This Child wont know about parentToDelete after this operation.
     * Set all {@link UniDirParentEntity}s of this {@link UniDirChild} to null.
     * @param parentToDelete
     * @throws UnknownParentTypeException   thrown, if parentToDelete is of unknown type -> no field , annotated as {@link UniDirParentEntity}, with the most specific type of parentToDelete, exists in Child (this).
     * @throws IllegalAccessException
     */
    public default void dismissParent(UniDirParent parentToDelete) throws UnknownParentTypeException, IllegalAccessException {
        AtomicBoolean parentRemoved = new AtomicBoolean(false);
        Field[] parentFields = findParentFields();
        for(Field parentField: parentFields){
            parentField.setAccessible(true);
            UniDirParent  parent = (UniDirParent) parentField.get(this);
            if(parent!=null) {
                if (parentToDelete.getClass().equals(parent.getClass())) {
                    parentField.set(this,null);
                    parentRemoved.set(true);
                }
            }
        }
        if(!parentRemoved.get()){
            throw new UnknownParentTypeException(this.getClass(),parentToDelete.getClass());
        }
    }
}
