package com.github.vincemann.springrapid.entityrelationship.model.child;

import com.github.vincemann.springrapid.entityrelationship.exception.UnknownParentTypeException;
import com.github.vincemann.springrapid.entityrelationship.model.parent.DirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.UniDirParentEntity;
import com.github.vincemann.springrapid.entityrelationship.util.EntityReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public interface DirChild {
    Logger log = LoggerFactory.getLogger(DirChild.class);

    public default void addParent(DirParent parentToSet, Class<? extends Annotation> parentEntityAnnotationClass) throws UnknownParentTypeException {
        AtomicBoolean parentSet = new AtomicBoolean(false);
        EntityReflectionUtils.doWithAnnotatedFieldsOfType(parentToSet.getClass(),parentEntityAnnotationClass,getClass(),field -> {
            field.set(this, parentToSet);
            parentSet.set(true);
        });
        if (!parentSet.get()) {
            throw new UnknownParentTypeException(this.getClass(), parentToSet.getClass());
        }
    }

    public default boolean addParentIfNull(DirParent parentToSet,Class<? extends Annotation> parentEntityAnnotationClass)  {
        AtomicBoolean added = new AtomicBoolean(false);
        EntityReflectionUtils.doWithAnnotatedFieldsOfType(parentToSet.getClass(),parentEntityAnnotationClass,getClass(),field -> {
            if(field.get(this)==null) {
                field.set(this, parentToSet);
                added.set(true);
            }
        });
        return added.get();
    }

    /**
     * @return all parent of this, that are not null
     */
    public default <P extends DirParent> Collection<P> findParents(Class<? extends Annotation> parentEntityAnnotationClass) {
        Collection<P> result = new ArrayList<>();
        EntityReflectionUtils.doWithAnnotatedFields(parentEntityAnnotationClass,getClass(),field -> {
            P parent = (P) field.get(this);
            if (parent != null) {
                result.add(parent);
            }
        });
        return result;
    }

    /**
     * This Child wont know about parentToDelete after this operation.
     * Sets {@link UniDirParentEntity} Field, that has same Type as {@param parentToDelete}, to null.
     *
     * @param parentToDelete
     * @throws UnknownParentTypeException thrown, if parentToDelete is of unknown type -> no field , annotated as {@link UniDirParentEntity}, with the most specific type of parentToDelete, exists in Child (this).
     */
    public default void dismissParent(DirParent parentToDelete,Class<? extends Annotation> parentEntityAnnotationClass) throws UnknownParentTypeException {
        AtomicBoolean parentRemoved = new AtomicBoolean(false);
        EntityReflectionUtils.doWithAnnotatedFields(parentEntityAnnotationClass,getClass(),field -> {
            DirParent parent = (DirParent) field.get(this);
            if (parent != null) {
                if (parentToDelete.getClass().equals(parent.getClass())) {
                    field.set(this, null);
                    parentRemoved.set(true);
                }
            }
        });
        if (!parentRemoved.get()) {
            throw new UnknownParentTypeException(this.getClass(), parentToDelete.getClass());
        }
    }
}
