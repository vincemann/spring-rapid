package com.github.vincemann.springrapid.autobidir.resolveid.annotation.parent;

import com.github.vincemann.springrapid.autobidir.resolveid.annotation.child.DirChildId;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@DirChildId
public @interface BiDirParentIdCollection {

    /**
     * Type of Children which belong to the annotated id Collection
     * @return
     */
    Class<? extends IdentifiableEntity> value();
}
