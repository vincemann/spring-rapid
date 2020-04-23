package io.github.vincemann.springrapid.coretest.service.result.matcher.compare;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;

/**
 * Offers Matchers for comparing two Entities, that can also be {@link EntityPlaceholder}s.
 */
public class CompareEntityMatchers {

    /**
     *
     * @param compareRoot supply own Entity
     * @return
     */
    public static CompareEntityMatcherContext compare(IdentifiableEntity compareRoot){
        return new CompareEntityMatcherContext(compareRoot);
    }

    public static CompareEntityMatcherContext compare(EntityPlaceholder rootCompareResolvable){
        return new CompareEntityMatcherContext(rootCompareResolvable);
    }

    /**
     *
     * @param compareRoot supply own Entity
     * @return
     */
    public static EntityPropertyMatcher propertyCompare(IdentifiableEntity compareRoot){
        return new EntityPropertyMatcher(compareRoot);
    }

    public static EntityPropertyMatcher propertyCompare(EntityPlaceholder rootCompareResolvable){
        return new EntityPropertyMatcher(rootCompareResolvable);
    }

}
