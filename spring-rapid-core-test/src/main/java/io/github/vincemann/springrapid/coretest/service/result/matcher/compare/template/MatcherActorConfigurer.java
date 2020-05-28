package io.github.vincemann.springrapid.coretest.service.result.matcher.compare.template;

import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;

public interface MatcherActorConfigurer {
    public MatcherAdditionalActorConfigurer with(Object actor);
    public MatcherAdditionalActorConfigurer with(EntityPlaceholder actor);
}
