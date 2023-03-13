package com.github.vincemann.springrapid.core.util;

import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

public class ArrayUtils {


    public static <T> Set<T> arrayToSet(T[] array) {
        Set<T> result = new HashSet<>();
        CollectionUtils.mergeArrayIntoCollection(array, result);
        return result;
    }
}
