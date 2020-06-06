package io.github.vincemann.springrapid.coretest.service.result.matcher.compare;

import io.github.vincemann.ezcompare.ActorBridge;
import io.github.vincemann.springrapid.coretest.service.result.matcher.resolve.EntityPlaceholder;

public interface PlaceholderResolvingActorConfigurer {
    public ActorBridge with(Object actor);
    public ActorBridge with(EntityPlaceholder actor);
}
