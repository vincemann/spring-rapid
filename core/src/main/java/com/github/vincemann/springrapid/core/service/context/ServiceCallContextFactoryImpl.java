package com.github.vincemann.springrapid.core.service.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ServiceCallContextFactoryImpl implements ServiceCallContextFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public ServiceCallContext create() {
        return applicationContext.getBean(ServiceCallContext.class);
    }
}
