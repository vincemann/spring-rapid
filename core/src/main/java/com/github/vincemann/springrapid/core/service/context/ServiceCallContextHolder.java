package com.github.vincemann.springrapid.core.service.context;

import org.springframework.security.core.context.*;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;

public class ServiceCallContextHolder {
    private static ServiceCallContextHolderStrategy strategy;
    private static int initializeCount = 0;

    static {
        initialize();
    }

    public static void clearContext() {
        strategy.clearContext();
    }


    public static ServiceCallContext getContext() {
        return strategy.getContext();
    }

    public static int getInitializeCount() {
        return initializeCount;
    }

    private static void initialize() {
       strategy = new ThreadLocalServiceCallContextHolderStrategy();

        initializeCount++;
    }

    public static void setContext(ServiceCallContext context) {
        strategy.setContext(context);
    }


    public static ServiceCallContextHolderStrategy getContextHolderStrategy() {
        return strategy;
    }
    public static ServiceCallContext createEmptyContext() {
        return strategy.createEmptyContext();
    }

}
