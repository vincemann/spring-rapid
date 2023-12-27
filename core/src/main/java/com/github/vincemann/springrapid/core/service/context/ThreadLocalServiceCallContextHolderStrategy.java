package com.github.vincemann.springrapid.core.service.context;

import org.springframework.util.Assert;

public class ThreadLocalServiceCallContextHolderStrategy implements ServiceCallContextHolderStrategy{
    private final ThreadLocal<ServiceCallContext> contextHolder = new ThreadLocal<>();
    private final ThreadLocal<SubServiceCallContext> subContextHolder = new ThreadLocal<>();

    public ThreadLocalServiceCallContextHolderStrategy() {}

    // ~ Methods
    // ========================================================================================================

    public void clearContext() {
        contextHolder.remove();
    }

    @Override
    public void clearSubContext() {
        subContextHolder.remove();
    }

    @Override
    public SubServiceCallContext getSubContext() {
        return subContextHolder.get();
    }

    @Override
    public void setSubContext(SubServiceCallContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        subContextHolder.set(context);
    }

    public ServiceCallContext getContext() {
        return contextHolder.get();

//        if (ctx == null) {
//            ctx = createEmptyContext();
//            contextHolder.set(ctx);
//        }
//        return ctx;
    }

    public void setContext(ServiceCallContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        contextHolder.set(context);
    }

}
