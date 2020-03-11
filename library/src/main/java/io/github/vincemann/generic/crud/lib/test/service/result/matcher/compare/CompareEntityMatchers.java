package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare.resolve.CompareEntityPlaceholder;

public class CompareEntityMatchers {

    /**
     *
     * @param compareRoot supply own Entity
     * @return
     */
    public static CompareEntityMatcher compare(IdentifiableEntity compareRoot){
        return new CompareEntityMatcher(compareRoot);
    }

    public static CompareEntityMatcher compare(CompareEntityPlaceholder rootCompareResolvable){
        return new CompareEntityMatcher(rootCompareResolvable);
    }

    /**
     *
     * @param compareRoot supply own Entity
     * @return
     */
    public static CompareEntityMatcher propertyCompare(IdentifiableEntity compareRoot){
        return new CompareEntityMatcher(compareRoot);
    }

    public static CompareEntityMatcher propertyCompare(CompareEntityPlaceholder rootCompareResolvable){
        return new CompareEntityMatcher(rootCompareResolvable);
    }

}
