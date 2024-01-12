package com.github.vincemann.springrapid.sync.util;

import java.lang.reflect.Field;
import java.util.*;

public class ReflectionUtils {

    public static Collection<?> createAndAddAll(Collection<?> inputCollection) {
        if (inputCollection instanceof Set<?>) {
            // If the input collection is a Set, create a new HashSet and add all elements
            Set<Object> newSet = new HashSet<>(inputCollection);
            return newSet;
        } else if (inputCollection instanceof List<?>) {
            // If the input collection is a List, create a new ArrayList and add all elements
            List<Object> newList = new ArrayList<>(inputCollection);
            return newList;
        } else {
            // If it's neither a Set nor a List, throw an exception
            throw new IllegalArgumentException("Unsupported collection type: " + inputCollection.getClass().getName());
        }
    }

    public static Collection<?> accessCollectionField(Object entity, Field field) {
//        Field collectionField = ReflectionUtils.findField(entity.getClass(), collectionFieldName);
//
//        if (collectionField == null) {
//            throw new IllegalArgumentException("Field '" + collectionFieldName + "' not found in class " + entity.getClass().getName());
//        }

        // Make the field accessible (even if it's private)
        org.springframework.util.ReflectionUtils.makeAccessible(field);

        // Get the value of the collection field from the entity object
        Object collectionValue = org.springframework.util.ReflectionUtils.getField(field, entity);
        if (collectionValue == null)
            return null;

        // Check if the field is a Collection (or a subtype of Collection)
        if (collectionValue instanceof Collection<?>) {
            // Cast the collection value to Collection<?> and return it
            return (Collection<?>) collectionValue;
        } else {
            // If the field is not a collection, you can handle the error accordingly
            throw new IllegalArgumentException("The specified field is not a collection.");
        }
    }
}
