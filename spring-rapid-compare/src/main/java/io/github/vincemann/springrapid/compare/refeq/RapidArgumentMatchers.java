package io.github.vincemann.springrapid.compare.refeq;

import org.mockito.ArgumentMatcher;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

public class RapidArgumentMatchers {

    /**
     * Same as {@link org.mockito.ArgumentMatchers#refEq(Object, String...)}, but logs minimal Diff, if objects differ.
     * Only takes fields from root (expected) object class into consideration.
     * Can compare diff Types, if field names match.
     * @param value
     * @param excludeFields
     * @param <T>
     * @return
     */
    public static <T> T refEq(T value, String... excludeFields) {
        reportMatcher(new RapidReflectionEquals(value, excludeFields));
        return null;
    }

    private static void reportMatcher(ArgumentMatcher<?> matcher) {
        mockingProgress().getArgumentMatcherStorage().reportMatcher(matcher);
    }
}
