package com.github.vincemann.springrapid.core.service.context;

public class ServiceCallContextHolder {
    private static ServiceCallContextHolderStrategy strategy;

    static {
        initialize();
    }

    public static void clearContext() {
        strategy.clearContext();
    }

    public static void clearSubContext() {
        strategy.clearSubContext();
    }

    public static boolean isContextInitialized(){
        return strategy.getContext() != null;
    }

    public static boolean isSubContextInitialized(){
        return strategy.getSubContext() != null;
    }



    public static <T extends ServiceCallContext> T getContext() {
        return (T) strategy.getContext();
    }

    public static SubServiceCallContext getSubContext() {
        return strategy.getSubContext();
    }


    /**
     * needs to be called before executing any method of this class.
     */
    public static void initialize() {
       strategy = new ThreadLocalServiceCallContextHolderStrategy();
    }

    public static void setContext(ServiceCallContext context) {
        strategy.setContext(context);
    }

    public static void setSubContext(SubServiceCallContext context) {
        strategy.setSubContext(context);
    }



    public static ServiceCallContextHolderStrategy getContextHolderStrategy() {
        return strategy;
    }

}
