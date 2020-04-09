package io.github.vincemann.springrapid.acl.proxy.noRuleStrategy;

import java.lang.reflect.Method;

public class ThrowExceptionNoSecurityRuleHandlingStrategy implements HandleNoSecurityRuleStrategy {
    @Override
    public void react(Method method) {
        throw new NoSecurityRuleException("No Security Rule in Security Proxy present for method: " + method);
    }
}
