package io.github.vincemann.generic.crud.lib.dto.biDir;

import io.github.vincemann.generic.crud.lib.model.biDir.BiDirChild;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BiDirChildIdCollection {

    /**
     * Type of Children which belong to the annotated id Collection
     * @return
     */
    Class<? extends BiDirChild> value();
}
