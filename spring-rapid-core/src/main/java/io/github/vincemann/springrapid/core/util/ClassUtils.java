package io.github.vincemann.springrapid.core.util;

import java.util.HashSet;
import java.util.Set;

public class ClassUtils {

    //whole class hierachy
    public static Class[] getAllInterfaces(Class clazz) {
        Class curr = clazz;
        Set<Class> interfaces = new HashSet<>();
        do {
            interfaces.addAll(Lists.newArrayList(curr.getInterfaces()));
            curr = curr.getSuperclass();
        } while (!curr.equals(Object.class));
        interfaces.forEach(i -> interfaces.addAll(Lists.newArrayList(i.getInterfaces())));
        return interfaces.toArray(new Class[0]);
    }
}
