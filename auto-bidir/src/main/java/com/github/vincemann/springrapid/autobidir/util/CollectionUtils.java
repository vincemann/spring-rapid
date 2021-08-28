package com.github.vincemann.springrapid.autobidir.util;

import java.lang.reflect.Field;
import java.util.*;


public class CollectionUtils {

    public static <T extends Collection> T createEmptyCollection(Field collectionField){
        Class<?> type = collectionField.getType();
        if(Set.class.isAssignableFrom(type)){
            return (T) new HashSet<>();
        }else if(List.class.isAssignableFrom(type)){
            return (T) new ArrayList<>();
        }else if(Map.class.isAssignableFrom(type)){
            return (T) new HashMap<>();
        }else {
            throw new IllegalArgumentException("unknown collection type");
        }
    }
}
