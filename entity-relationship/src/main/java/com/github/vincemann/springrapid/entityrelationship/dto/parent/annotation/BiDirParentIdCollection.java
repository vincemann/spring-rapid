package com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation;

import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.DirChildId;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@DirChildId
public @interface BiDirParentIdCollection {

    /**
     * Type of Children which belong to the annotated id Collection
     * @return
     */
    Class<? extends BiDirParent> value();
}
