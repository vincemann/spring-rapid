package io.github.vincemann.generic.crud.lib.model.uniDir.child;

import io.github.vincemann.generic.crud.lib.model.uniDir.UniDirEntity;
import io.github.vincemann.generic.crud.lib.model.uniDir.parent.UniDirParent;
import io.github.vincemann.generic.crud.lib.model.uniDir.parent.UniDirParentEntity;
import io.github.vincemann.generic.crud.lib.service.exception.entityRelationHandling.UnknownParentTypeException;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public interface UniDirChild extends UniDirEntity {
    Logger log = LoggerFactory.getLogger(UniDirChild.class);
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
     * Find Parent of this child, annotated with {@link UniDirParentEntity} that has the same type as {@param parentToSet}.
     * Does not override. The parent will only be set to {@param parentToSet}, if matching parentField's value is null.
     * @param parentToSet
     * @return  true, if parent was null and is set to {@param parentToSet}, otherwise false
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
    public default Collection findParents() throws IllegalAccessException {
        Collection result = new ArrayList<>();
        Field[] parentFields = findParentFields();
        for(Field parentField: parentFields) {
            parentField.setAccessible(true);
            Object uniDirParent =  parentField.get(this);
            if(uniDirParent!=null) {
                result.add(uniDirParent);
            }
        }
        return result;
    }

    /**
     * This Child wont know about parentToDelete after this operation.
     * Sets {@link UniDirParentEntity} Field, that has same Type as {@param parentToDelete}, to null.
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
