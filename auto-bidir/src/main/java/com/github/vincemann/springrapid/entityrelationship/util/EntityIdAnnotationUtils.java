package com.github.vincemann.springrapid.entityrelationship.util;

import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildId;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.UniDirChildId;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.UniDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentIdCollection;

import java.lang.annotation.Annotation;

public class EntityIdAnnotationUtils {

    public static Class<?> getEntityType(Annotation annotation){
        if (annotation instanceof BiDirChildId){
            return ((BiDirChildId) annotation).value();
        }else if (annotation instanceof BiDirChildIdCollection){
            return ((BiDirChildIdCollection) annotation).value();
        }

        else if (annotation instanceof BiDirParentId){
            return ((BiDirParentId) annotation).value();
        }else if (annotation instanceof BiDirParentIdCollection){
            return ((BiDirParentIdCollection) annotation).value();
        }


        else if (annotation instanceof UniDirChildId){
            return ((UniDirChildId) annotation).value();
        }
        else if (annotation instanceof UniDirChildIdCollection){
            return ((UniDirChildIdCollection) annotation).value();
        }
        throw new IllegalArgumentException("Annotation: " + annotation + " is not of EntityId Type");
    }
}
