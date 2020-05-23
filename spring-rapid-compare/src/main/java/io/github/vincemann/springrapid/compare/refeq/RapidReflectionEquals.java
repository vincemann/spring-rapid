package io.github.vincemann.springrapid.compare.refeq;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mockito.ArgumentMatcher;

import java.io.Serializable;

/**
 * @see RapidArgumentMatchers#refEq(Object, String...)
 */
@Slf4j
public class RapidReflectionEquals implements ArgumentMatcher<Object>, Serializable {

    private final Object wanted;
    private final String[] excludeFields;
    @Getter
    private RapidEqualsBuilder.MinimalDiff minimalDiff;

    public RapidReflectionEquals(Object wanted, String... excludeFields) {
        this.wanted = wanted;
        this.excludeFields = excludeFields;
    }

    public boolean matches(Object actual) {
        minimalDiff = RapidEqualsBuilder.reflectionEquals(wanted, actual, excludeFields);
        if (minimalDiff.isDifferent()) {
            log.debug("Wanted: " + wanted + "and actual: " + actual + " differ:");
            log.debug(minimalDiff.toString());
        }
        return minimalDiff.isEmpty();
    }

    public String toString() {
        return "refEq(" + wanted + ")";
    }
}
