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
    public boolean isEqual(T request, T updated) {
        try {
            //order in Lists is ignored
            ReflectionAssert.assertReflectionEquals(request, updated, ReflectionComparatorMode.LENIENT_ORDER);
            return true;
        } catch (AssertionFailedError e) {
            log.debug("Objects are not considered equal by EqualChecker: " + e.getMessage());
            return false;
        }
    }
}
