package io.github.vincemann.springrapid.acl.proxy.noRuleStrategy;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.acl.proxy.CrudServiceSecurityProxy;

import java.lang.reflect.Method;

/**
 * This Strategy handles the case, when so {@link io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule}
 * was applicable for {@link CrudServiceSecurityProxy}.
 */
@ServiceComponent
public interface HandleNoSecurityRuleStrategy {
    public void react(Method method);
}
