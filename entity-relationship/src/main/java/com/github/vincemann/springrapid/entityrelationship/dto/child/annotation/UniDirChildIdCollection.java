package com.github.vincemann.springrapid.entityrelationship.dto.child.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniDirChildIdCollection {
    /**
     * Type of Child which belongs to the annotated child id Collection
     * @return
     */
    Class value();
}
