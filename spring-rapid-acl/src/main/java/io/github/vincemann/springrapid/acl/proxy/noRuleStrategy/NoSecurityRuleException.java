package io.github.vincemann.springrapid.acl.proxy.noRuleStrategy;

import org.springframework.security.access.AccessDeniedException;

public class NoSecurityRuleException extends AccessDeniedException {

    public NoSecurityRuleException(String msg) {
        super(msg);
    }

    public NoSecurityRuleException(String msg, Throwable t) {
        super(msg, t);
    }
}
