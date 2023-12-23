package com.github.vincemann.springrapid.core.service.context;

public class ServiceCallContextHolder {
    private static ServiceCallContextHolderStrategy strategy;
    private static int initializeCount = 0;


    public static void clearContext() {
        strategy.clearContext();
    }


    public static <T extends ServiceCallContext> T getContext() {
        return (T) strategy.getContext();
    }

    public static int getInitializeCount() {
        return initializeCount;
    }

    /**
     * needs to be called before executing any method of this class.
     */
    public static void initialize(ServiceCallContextFactory serviceCallContextFactory) {
       strategy = new ThreadLocalServiceCallContextHolderStrategy(serviceCallContextFactory);
       initializeCount++;
    }

    public static void setContext(ServiceCallContext context) {
        strategy.setContext(context);
    }

    public static void setEmptyContext(){
        setContext(createEmptyContext());
    }


    public static ServiceCallContextHolderStrategy getContextHolderStrategy() {
        return strategy;
    }
    public static ServiceCallContext createEmptyContext() {
        return strategy.createEmptyContext();
    }

}
