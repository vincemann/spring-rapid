package com.github.vincemann.springrapid.entityrelationship.model.parent.annotation;

import com.github.vincemann.springrapid.entityrelationship.model.parent.UniDirParent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @see UniDirParent
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniDirParentEntity {
}
