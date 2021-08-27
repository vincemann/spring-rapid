package com.github.vincemann.springrapid.entityrelationship.model.parent.annotation;

import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see BiDirParent
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BiDirParentEntity {
}
