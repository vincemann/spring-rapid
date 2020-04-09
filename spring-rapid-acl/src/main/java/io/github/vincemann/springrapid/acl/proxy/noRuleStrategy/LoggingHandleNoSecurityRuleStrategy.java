package io.github.vincemann.springrapid.acl.proxy.noRuleStrategy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class LoggingHandleNoSecurityRuleStrategy implements HandleNoSecurityRuleStrategy {

    @Override
    public void react(Method method) {
        log.debug("No security rule applies for: " + method + ", ignored and allowed by SecurityProxy ");
    }
}
