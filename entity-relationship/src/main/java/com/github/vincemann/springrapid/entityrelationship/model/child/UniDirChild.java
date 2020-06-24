package com.github.vincemann.springrapid.entityrelationship.model.child;

import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.UniDirEntity;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.UniDirParentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Same as {@link BiDirChild} but for a unidirectional relationship.
 */
public interface UniDirChild extends UniDirEntity, DirChild {
    Logger log = LoggerFactory.getLogger(UniDirChild.class);
//    Map<Class, Field[]> uniDirParentFieldsCache = new HashMap<>();

    /**
     * @param parentToSet
     * @throws UnknownParentTypeException when supplied Parent does not match any of the fields in child class anntoated with {@link UniDirParentEntity}
     */
    public default void addUniDirParent(Object parentToSet) throws UnknownParentTypeException {
        AtomicBoolean parentSet = new AtomicBoolean(false);
        ReflectionUtils.doWithFields(getClass(), field -> {
            if (parentToSet.getClass().equals(field.getType())) {
                ReflectionUtils.makeAccessible(field);
                field.set(this, parentToSet);
                parentSet.set(true);
            }
        }, new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(UniDirParentEntity.class));
        if (!parentSet.get()) {
            throw new UnknownParentTypeException(this.getClass(), parentToSet.getClass());
        }

//        AtomicBoolean parentSet = new AtomicBoolean(false);
//        for(Field parentField: _findParentFields()){
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
     * Find Parent of this child, annotated with {@link UniDirParentEntity} that has the same type as {@param parentToSet}.
     * Does not override. The parent will only be set to {@param parentToSet}, if matching parentField's value is null.
     *
     * @param parentToSet
     * @return true, if parent was null and is set to {@param parentToSet}, otherwise false
     */
    public default boolean addUniDirParentIfNull(UniDirParent parentToSet) {
        AtomicBoolean parentSet = new AtomicBoolean(false);
        ReflectionUtils.doWithFields(getClass(), field -> {
            if (parentToSet.getClass().equals(field.getType())) {
                ReflectionUtils.makeAccessible(field);
                if (field.get(this) == null) {
                    parentSet.set(true);
                    field.set(this, parentToSet);
                }
            }
        }, new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(UniDirParentEntity.class));
//        for(Field parentField: _findParentFields()){
//            if(parentToSet.getClass().equals(parentField.getType())){
//                parentField.setAccessible(true);
//                if(parentField.get(this)==null) {
//                    parentField.set(this, parentToSet);
//                }
//            }
//        }
        return parentSet.get();
    }

//    /**
//     * Find all fields of this child, annotated with {@link UniDirParentEntity}
//     * @return
//     */
//    public default Field[] _findParentFields(){
//        Field[] parentFieldsFromCache = uniDirParentFieldsCache.get(this.getClass());
//        if(parentFieldsFromCache==null){
//            Field[] parentFields = ReflectionUtilsBean.getInstance().getFieldsWithAnnotation(getClass(), UniDirParentEntity.class);
//            uniDirParentFieldsCache.put(this.getClass(),parentFields);
//            return parentFields;
//        }else {
//            return parentFieldsFromCache;
//        }
//    }


    /**
     * @return all parent of this, that are not null
     */
    public default Collection<UniDirParent> findUniDirParents() {
        Collection<UniDirParent> result = new ArrayList<>();
        ReflectionUtils.doWithFields(getClass(), field -> {
            ReflectionUtils.makeAccessible(field);
            UniDirParent uniDirParent = (UniDirParent) field.get(this);
            if (uniDirParent != null) {
                result.add(uniDirParent);
            }
        }, new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(UniDirParentEntity.class));
//        Collection result = new ArrayList<>();
//        Field[] parentFields = _findParentFields();
//        for (Field parentField : parentFields) {
//            parentField.setAccessible(true);
//            Object uniDirParent = parentField.get(this);
//            if (uniDirParent != null) {
//                result.add(uniDirParent);
//            }
//        }
        return result;
    }

    /**
     * This Child wont know about parentToDelete after this operation.
     * Sets {@link UniDirParentEntity} Field, that has same Type as {@param parentToDelete}, to null.
     *
     * @param parentToDelete
     * @throws UnknownParentTypeException thrown, if parentToDelete is of unknown type -> no field , annotated as {@link UniDirParentEntity}, with the most specific type of parentToDelete, exists in Child (this).
     */
    public default void dismissUniDirParent(UniDirParent parentToDelete) throws UnknownParentTypeException {
        AtomicBoolean parentRemoved = new AtomicBoolean(false);
        ReflectionUtils.doWithFields(getClass(), field -> {
            ReflectionUtils.makeAccessible(field);
            UniDirParent parent = (UniDirParent) field.get(this);
            if (parent != null) {
                if (parentToDelete.getClass().equals(parent.getClass())) {
                    field.set(this, null);
                    parentRemoved.set(true);
                }
            }
        }, new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(UniDirParentEntity.class));
//        Field[] parentFields = _findParentFields();
//        for (Field parentField : parentFields) {
//            parentField.setAccessible(true);
//            UniDirParent parent = (UniDirParent) parentField.get(this);
//            if (parent != null) {
//                if (parentToDelete.getClass().equals(parent.getClass())) {
//                    parentField.set(this, null);
//                    parentRemoved.set(true);
//                }
//            }
//        }
        if (!parentRemoved.get()) {
            throw new UnknownParentTypeException(this.getClass(), parentToDelete.getClass());
        }
    }
}
