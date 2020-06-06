package io.github.vincemann.springrapid.coretest.service.result.matcher.compare;

import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;

public interface MatcherPropertyConfigurer extends MatcherSelectingPropertyConfigurer {
    MatcherIgnoringPropertyConfigurer allOf(Object o);
    MatcherIgnoringPropertyConfigurer allOf(EntityPlaceholder o);
    MatcherIgnoringPropertyConfigurer all();
}
