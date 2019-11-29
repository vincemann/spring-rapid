package io.github.vincemann.generic.crud.lib.test.equalChecker;

import junit.framework.AssertionFailedError;
import org.springframework.stereotype.Component;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

@Component
public class ReflectionEqualChecker<T> implements EqualChecker<T> {

    @Override
    public boolean isEqual(T o1, T o2) {
        try {
            //Reihenfolge in Lists wird hier ignored
            ReflectionAssert.assertReflectionEquals(o1, o2, ReflectionComparatorMode.LENIENT_ORDER);
            return true;
        } catch (AssertionFailedError e) {
            //e.printStackTrace();
            return false;
        }
    }
}
