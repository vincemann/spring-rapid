package com.github.vincemann.springrapid.core.service.context;

import org.springframework.security.core.context.SecurityContext;

public interface ServiceCallContextHolderStrategy {
    void clearContext();

    void clearSubContext();

    ServiceCallContext getContext();

    SubServiceCallContext getSubContext();

    void setContext(ServiceCallContext context);
    void setSubContext(SubServiceCallContext context);
}
