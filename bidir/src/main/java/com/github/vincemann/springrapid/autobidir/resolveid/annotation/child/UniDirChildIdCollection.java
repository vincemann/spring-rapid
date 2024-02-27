package com.github.vincemann.springrapid.autobidir.resolveid.annotation.child;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@DirChildId
public @interface UniDirChildIdCollection {
    /**
     * Type of Child which belongs to the annotated child id Collection
     * @return
     */
    Class value();
}
