package com.github.vincemann.springrapid.entityrelationship.util;

import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.UniDirChildCollection;

import java.lang.annotation.Annotation;

public class EntityAnnotationUtils {

    public static Class<?> getEntityType(Annotation annotation){
        if (annotation instanceof BiDirChildCollection){
            return ((BiDirChildCollection) annotation).value();
        }else if (annotation instanceof UniDirChildCollection) {
            return ((UniDirChildCollection) annotation).value();
        }
        throw new IllegalArgumentException("Annotation: " + annotation + " is not of EntityCollection Type");
    }
}
