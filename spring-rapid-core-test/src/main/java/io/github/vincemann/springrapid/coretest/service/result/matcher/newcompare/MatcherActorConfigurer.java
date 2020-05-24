package io.github.vincemann.springrapid.coretest.service.result.matcher.newcompare;

import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;

public interface MatcherActorConfigurer {
    public MatcherOptionalActorConfigurer with(Object actor);
    public MatcherOptionalActorConfigurer with(EntityPlaceholder actor);
}
