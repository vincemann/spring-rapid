package com.github.vincemann.springrapid.autobidir.util;

import com.github.vincemann.springrapid.autobidir.dto.child.annotation.BiDirChildId;
import com.github.vincemann.springrapid.autobidir.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.autobidir.dto.child.annotation.UniDirChildId;
import com.github.vincemann.springrapid.autobidir.dto.child.annotation.UniDirChildIdCollection;
import com.github.vincemann.springrapid.autobidir.dto.parent.annotation.BiDirParentId;
import com.github.vincemann.springrapid.autobidir.dto.parent.annotation.BiDirParentIdCollection;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RelationalEntityIdAnnotationUtils {
    private static final Map<Annotation, Class<?>> annotationTypeCache = new ConcurrentHashMap<>();

    public static Class<?> getEntityType(Annotation annotation){
        Class<?> cachedType = annotationTypeCache.get(annotation);
        if (cachedType != null) {
            return cachedType;
        }

        Class<?> entityType = null;
        if (annotation instanceof BiDirChildId) {
            entityType = ((BiDirChildId) annotation).value();
        } else if (annotation instanceof BiDirChildIdCollection) {
            entityType = ((BiDirChildIdCollection) annotation).value();
        } else if (annotation instanceof BiDirParentId) {
            entityType = ((BiDirParentId) annotation).value();
        } else if (annotation instanceof BiDirParentIdCollection) {
            entityType = ((BiDirParentIdCollection) annotation).value();
        } else if (annotation instanceof UniDirChildId) {
            entityType = ((UniDirChildId) annotation).value();
        } else if (annotation instanceof UniDirChildIdCollection) {
            entityType = ((UniDirChildIdCollection) annotation).value();
        }

        if (entityType != null) {
            annotationTypeCache.put(annotation, entityType);
            return entityType;
        }

        throw new IllegalArgumentException("Annotation: " + annotation + " is not of EntityId Type");
    }
}
