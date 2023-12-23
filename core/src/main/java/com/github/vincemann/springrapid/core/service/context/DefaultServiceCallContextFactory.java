package com.github.vincemann.springrapid.core.service.context;

public class DefaultServiceCallContextFactory implements ServiceCallContextFactory {

    @Override
    public ServiceCallContext create() {
        return new ServiceCallContext();
    }
}
