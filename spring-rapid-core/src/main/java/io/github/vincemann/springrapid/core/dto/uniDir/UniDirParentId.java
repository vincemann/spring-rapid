package io.github.vincemann.springrapid.core.dto.uniDir;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniDirParentId {

    /**
     * Type of Parent which belongs to the annotated parent id
     * @return
     */
    Class value();

}
