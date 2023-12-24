package com.github.vincemann.springrapid.core.service.context;

import org.springframework.security.core.context.SecurityContext;

public interface ServiceCallContextHolderStrategy {
    void clearContext();

    ServiceCallContext getContext();

    void setContext(ServiceCallContext context);
}
