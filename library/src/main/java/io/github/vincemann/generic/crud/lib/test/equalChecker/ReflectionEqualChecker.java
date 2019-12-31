package io.github.vincemann.generic.crud.lib.test.equalChecker;

import junit.framework.AssertionFailedError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

@Component
@Slf4j
public class ReflectionEqualChecker<T> implements EqualChecker<T> {

    @Override
    public boolean isEqual(T o1, T o2) {
        try {
            //order in Lists is ignored
            ReflectionAssert.assertReflectionEquals(o1, o2, ReflectionComparatorMode.LENIENT_ORDER);
            return true;
        } catch (AssertionFailedError e) {
            log.debug("Objects are not considered equal by EqualChecker: " + e.getMessage());
            return false;
        }
    }
}
