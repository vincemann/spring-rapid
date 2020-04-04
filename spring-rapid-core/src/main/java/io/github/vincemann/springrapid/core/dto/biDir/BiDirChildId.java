package io.github.vincemann.springrapid.core.dto.biDir;

import io.github.vincemann.springrapid.core.model.biDir.child.BiDirChild;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BiDirChildId {

    /**
     * Type of Child which belongs to the annotated child id
     * @return
     */
    Class<? extends BiDirChild> value();
}
