package com.github.vincemann.springrapid.entityrelationship.model.child.annotation;

import com.github.vincemann.springrapid.entityrelationship.model.child.UniDirChild;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @see UniDirChild
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniDirChildEntity {
}
