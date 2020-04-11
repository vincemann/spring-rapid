package io.github.vincemann.springrapid.core.util;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class Lists {
    @SafeVarargs
    @GwtCompatible(
            serializable = true
    )
    public static <E> ArrayList<E> newArrayList(E... elements) {
        int capacity = elements==null ? computeArrayListCapacity(1) : computeArrayListCapacity(elements.length);
        ArrayList<E> list = new ArrayList(capacity);
        if(elements==null){
            list.add(null);
        }else {
            Collections.addAll(list, elements);
        }
        return list;
    }
    @GwtCompatible(
            serializable = true
    )
    public static <E> ArrayList<E> newArrayList(Collection<? extends E> elements) {
        return new ArrayList(elements);
    }


    @VisibleForTesting
    static int computeArrayListCapacity(int arraySize) {
        return Ints.saturatedCast(5L + (long)arraySize + (long)(arraySize / 10));
    }
}
