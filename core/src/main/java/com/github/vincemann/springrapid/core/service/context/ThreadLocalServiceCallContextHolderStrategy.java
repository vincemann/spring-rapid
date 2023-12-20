package com.github.vincemann.springrapid.core.service.context;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.util.Assert;

public class ThreadLocalServiceCallContextHolderStrategy implements ServiceCallContextHolderStrategy{
    private static final ThreadLocal<ServiceCallContext> contextHolder = new ThreadLocal<>();

    // ~ Methods
    // ========================================================================================================

    public void clearContext() {
        contextHolder.remove();
    }

    public ServiceCallContext getContext() {
        ServiceCallContext ctx = contextHolder.get();

        if (ctx == null) {
            ctx = createEmptyContext();
            contextHolder.set(ctx);
        }

        return ctx;
    }

    public void setContext(ServiceCallContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        contextHolder.set(context);
    }

    public ServiceCallContext createEmptyContext() {
        return new ServiceCallContext();
    }
}
