package com.github.vincemann.springrapid.entityrelationship.model.parent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniDirParentCollection {

    /**
     *
     * @return generic type of annotated collection
     */
    Class value();
}
