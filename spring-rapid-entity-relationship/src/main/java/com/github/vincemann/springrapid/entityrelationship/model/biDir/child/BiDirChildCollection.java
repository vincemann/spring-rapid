package com.github.vincemann.springrapid.entityrelationship.model.biDir.child;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see BiDirChild
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BiDirChildCollection {

    /**
     *
     * @return generic type of annotated collection
     */
    Class<? extends BiDirChild> value();
}
