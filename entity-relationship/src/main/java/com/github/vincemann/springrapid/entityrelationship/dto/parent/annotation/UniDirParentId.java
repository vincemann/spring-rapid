package com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@DirParentId
public @interface UniDirParentId {

    /**
     * Type of Parent which belongs to the annotated parent id
     * @return
     */
    Class value();

}
