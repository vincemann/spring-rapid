package com.github.vincemann.springrapid.autobidir.entity.annotation.child;


import com.github.vincemann.springrapid.core.model.IdAwareEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BiDirChildCollection {

    /**
     *
     * @return generic type of annotated collection
     */
    Class<? extends IdAwareEntity> value();
}
