package io.github.vincemann.springrapid.coretest.service.result.matcher.compare;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.coretest.service.PlaceholderResolvingComparison;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;

/**
 * Offers Matchers for comparing two Entities, that can also be {@link EntityPlaceholder}s.
 */
public class CompareMatchers {

    /**
     *
     * @param compareRoot supply own Entity
     * @return
     */
    public static PlaceholderResolvingActorConfigurer compare(IdentifiableEntity compareRoot){
        return new PlaceholderResolvingComparison(compareRoot);
    }

    public static PlaceholderResolvingActorConfigurer compare(EntityPlaceholder rootCompareResolvable){
        return new PlaceholderResolvingComparison(rootCompareResolvable);
    }

    /**
     *
     * @param compareRoot supply own Entity
     * @return
     */
    public static PlaceholderResolvingPropertyMatcher propertyCompare(IdentifiableEntity compareRoot){
        return new PlaceholderResolvingPropertyMatcher(compareRoot);
    }

    public static PlaceholderResolvingPropertyMatcher propertyCompare(EntityPlaceholder rootCompareResolvable){
        return new PlaceholderResolvingPropertyMatcher(rootCompareResolvable);
    }

}
