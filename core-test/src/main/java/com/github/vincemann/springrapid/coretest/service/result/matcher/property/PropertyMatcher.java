package com.github.vincemann.springrapid.coretest.service.result.matcher.property;

import com.github.hervian.reflection.Types;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Asserts GetterValues of Object.
 */
public class PropertyMatcher {
    private Object compareRoot;


    public PropertyMatcher(IdentifiableEntity compareRoot) {
        this.compareRoot = compareRoot;
    }

    public PropertyMatcher shouldMatch(Types.Supplier<?> getter, Object expected) {
        Assertions.assertEquals(expected, call(getter));
        return this;
    }

    public PropertyMatcher shouldNotMatch(Types.Supplier<?> getter, Object unexpected) {
        Assertions.assertNotEquals(unexpected, call(getter));
        return this;
    }


    public PropertyMatcher shouldMatchSize(Types.Supplier<?> getter, int collectionSize) {
        Collection<?> collection = call(getter);
        Assertions.assertEquals(collectionSize,collection.size());
        return this;
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
