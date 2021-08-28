package com.github.vincemann.springrapid.autobidir.dto.child.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@DirChildId
public @interface UniDirChildId {

    /**
     * Type of Child which belongs to the annotated child id
     * @return
     */
    Class value();

}
