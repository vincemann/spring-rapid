package com.github.vincemann.springrapid.autobidir.resolveid.annotation.parent;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@DirParentId
public @interface BiDirParentId {
    /**
     * Type of Parent which belongs to the annotated parent id
     * @return
     */
    Class<? extends IdentifiableEntity> value();
}
