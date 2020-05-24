package io.github.vincemann.springrapid.coretest.service.result.matcher.compare.template;

import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;

public interface MatcherActorConfigurer {
    public MatcherOptionalActorConfigurer with(Object actor);
    public MatcherOptionalActorConfigurer with(EntityPlaceholder actor);
}
