package com.github.vincemann.springrapid.entityrelationship.util;

import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.UniDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentCollection;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.UniDirParentCollection;

import java.lang.annotation.Annotation;

public class EntityAnnotationUtils {

    public static Class<?> getEntityType(Annotation annotation){
        if (annotation instanceof BiDirChildCollection){
            return ((BiDirChildCollection) annotation).value();
        }else if (annotation instanceof UniDirChildCollection) {
            return ((UniDirChildCollection) annotation).value();
        }
        else if (annotation instanceof BiDirParentCollection) {
            return ((BiDirParentCollection) annotation).value();
        }
        else if (annotation instanceof UniDirParentCollection) {
            return ((UniDirParentCollection) annotation).value();
        }
        throw new IllegalArgumentException("Annotation: " + annotation + " is not of EntityCollection Type");
    }
}
