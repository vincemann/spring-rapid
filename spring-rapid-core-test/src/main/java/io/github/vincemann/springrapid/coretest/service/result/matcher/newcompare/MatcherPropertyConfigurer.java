package io.github.vincemann.springrapid.coretest.service.result.matcher.newcompare;

import io.github.vincemann.springrapid.compare.template.IgnoringPropertyConfigurer;

public interface MatcherPropertyConfigurer extends MatcherSelectingPropertyConfigurer {
    MatcherIgnoringPropertyConfigurer allOf(Object o);
    MatcherIgnoringPropertyConfigurer all();
}
