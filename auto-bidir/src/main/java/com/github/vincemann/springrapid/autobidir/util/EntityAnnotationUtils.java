package com.github.vincemann.springrapid.autobidir.util;

import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.UniDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.parent.annotation.BiDirParentCollection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

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
        throw new IllegalArgumentException("Annotation: " + annotation + " is not of EntityCollection Type");
    }

    public static Class<?> getEntityType(Field field){
        if (field.isAnnotationPresent(BiDirChildCollection.class)){
            return field.getAnnotation(BiDirChildCollection.class).value();
        }else if (field.isAnnotationPresent(BiDirParentCollection.class)){
            return field.getAnnotation(BiDirParentCollection.class).value();
        }
        else if (field.isAnnotationPresent(UniDirChildCollection.class)){
            return field.getAnnotation(UniDirChildCollection.class).value();
        }
        return null;
    }
}
