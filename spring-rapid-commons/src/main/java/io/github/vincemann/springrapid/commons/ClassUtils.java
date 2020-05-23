package io.github.vincemann.springrapid.commons;

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
        Set<Class> interfacesTmp = new HashSet<>(interfaces.size());
        interfaces.forEach(i -> interfacesTmp.addAll(Lists.newArrayList(i.getInterfaces())));
        interfacesTmp.forEach(i -> interfaces.add(i));
        return interfaces.toArray(new Class[0]);
    }
}
