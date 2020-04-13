package io.github.vincemann.springrapid.entityrelationship.model.uniDir.parent;

import io.github.vincemann.springrapid.entityrelationship.model.uniDir.child.UniDirChild;

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
