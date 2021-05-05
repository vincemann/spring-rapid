package com.github.vincemann.springrapid.coretest.service.result.matcher.property;

import com.github.hervian.reflection.Types;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Asserts GetterValues of Object.
 */
public class PropertyMatcher {
    private Object compareRoot;

    //todo add support for propertyname in string form instead of getter method ref

    public PropertyMatcher(Object compareRoot) {
        this.compareRoot = compareRoot;
    }

//    @AllArgsConstructor
//    static class Pair{
//        Types.Supplier<?> getter;
//        Object compare;
//    }
//
//    public static Pair pair(Types.Supplier<?> getter, Object expected){
//        return new Pair(getter,expected);
//    }
//
//    public PropertyMatcher assertEquals(Pair... pairs) {
//        for (Pair pair : pairs) {
//            Assertions.assertEquals(pair.compare, call(pair.getter));
//        }
//        return this;
//    }

    public PropertyMatcher assertEquals(Types.Supplier<?> getter, Object expected) {
        Assertions.assertEquals(expected, call(getter));
        return this;
    }
    public PropertyMatcher assertNull(Types.Supplier<?>... getter) {
        for (Types.Supplier<?> supplier : getter) {
            Assertions.assertNull(call(supplier));
        }
        return this;
    }

    public PropertyMatcher assertNotNull(Types.Supplier<?>... getter) {
        for (Types.Supplier<?> supplier : getter) {
            Assertions.assertNotNull(call(supplier));
        }
        return this;
    }


    public PropertyMatcher assertNotEquals(Types.Supplier<?> getter, Object unexpected) {
        Assertions.assertNotEquals(unexpected, call(getter));
        return this;
    }


    public PropertyMatcher assertSize(Types.Supplier<?> getter, int collectionSize) {
        Collection<?> collection = call(getter);
        Assertions.assertNotNull(collection);
        Assertions.assertEquals(collectionSize,collection.size());
        return this;
    }

    public PropertyMatcher assertContains(Types.Supplier<?> getter, Object... expected) {
        Collection<?> collection = call(getter);
        Assertions.assertNotNull(collection);
        for (Object object : expected) {
            Assertions.assertTrue(collection.contains(object));
        }
        return this;
    }


    public PropertyMatcher assertEmpty(Types.Supplier<?> getter) {
        return assertSize(getter,0);
    }

    private <T> T call(Types.Supplier<?> getter) {
        try {
            Method getterMethod = Types.createMethod(getter);
            return (T) getterMethod.invoke(compareRoot);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
