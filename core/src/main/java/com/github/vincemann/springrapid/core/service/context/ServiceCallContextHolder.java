package com.github.vincemann.springrapid.core.service.context;

public class ServiceCallContextHolder {
    private static ServiceCallContextHolderStrategy strategy;
    private static int initializeCount = 0;

    static {
        initialize();
    }

    public static void clearContext() {
        strategy.clearContext();
    }

    public static boolean isInitialized(){
        return strategy.getContext() != null;
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
    public static void initialize() {
       strategy = new ThreadLocalServiceCallContextHolderStrategy();
       initializeCount++;
    }

    public static void setContext(ServiceCallContext context) {
        strategy.setContext(context);
    }



    public static ServiceCallContextHolderStrategy getContextHolderStrategy() {
        return strategy;
    }

}
