package io.github.vincemann.springrapid.core.test.service.result.matcher.compare;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.test.service.result.matcher.compare.resolve.CompareEntityPlaceholder;

public class CompareEntityMatchers {

    /**
     *
     * @param compareRoot supply own Entity
     * @return
     */
    public static CompareEntityMatcherContext compare(IdentifiableEntity compareRoot){
        return new CompareEntityMatcherContext(compareRoot);
    }

    public static CompareEntityMatcherContext compare(CompareEntityPlaceholder rootCompareResolvable){
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

    public static EntityPropertyMatcher propertyCompare(CompareEntityPlaceholder rootCompareResolvable){
        return new EntityPropertyMatcher(rootCompareResolvable);
    }

}
