package io.github.vincemann.springrapid.entityrelationship.dto.biDir;

import io.github.vincemann.springrapid.entityrelationship.model.biDir.parent.BiDirParent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BiDirParentId {
    /**
     * Type of Parent which belongs to the annotated parent id
     * @return
     */
    Class<? extends BiDirParent> value();
}
