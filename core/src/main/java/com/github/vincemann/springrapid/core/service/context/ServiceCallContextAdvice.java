package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Stack;

/**
 * Sets {@link ServiceCallContext} in {@link ServiceCallContextHolder} for each service call.
 * hooks onto every service call for services implementing {@link CrudService}.
 * Lifetime of {@link ServiceCallContext} is the same as the most outer service call within the thread.
 * -> each Thread has own context
 * <p>
 * context is created to allow thread wide caching especially between extensions within an extension chain.
 * i.E. extensions hook deleteById and all need to call findById(id) to operate on the entity -> multiple uncached findById calls
 */
@Aspect
@Slf4j
@Order(1)
public class ServiceCallContextAdvice {

    private ThreadLocal<Stack<SubServiceCallContext>> subServiceCallStack = ThreadLocal.withInitial(Stack::new);
    private ServiceCallContextFactory serviceCallContextFactory;


    @Autowired
    public void setServiceCallContextFactory(ServiceCallContextFactory serviceCallContextFactory) {
        this.serviceCallContextFactory = serviceCallContextFactory;
    }

    // dont ignore proxies because context should be created when most outer proxy is called
    @Around(value = "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() " +
            // jdkProxies dont match, because they wont be wrapped with glibc aop proxies - so look out for most outer extension
//            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions()" +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreHelperServiceMethods() "
    )
    public Object aroundServiceOperation(ProceedingJoinPoint joinPoint) throws Throwable {
//        System.err.println("SERVICE CALL CONTEXT: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        // jdkProxies dont match, because they wont be wrapped with glibc aop proxies - so look out for most outer extension
        if (joinPoint.getTarget() instanceof AbstractServiceExtension) {
            boolean init = initGlobalContext();
            if (init){
                initSubContext();
            }
            return joinPoint.proceed();
        }

        // maybe no extension matched, for example root service direct called -> context would then be initialized here
        initGlobalContext();
        initSubContext();



        Object ret;
        try {
            ret = joinPoint.proceed();
        } finally {
            // restore old, or clear if last
            subServiceCallStack.get().pop();
            if (subServiceCallStack.get().size() > 0) {
                SubServiceCallContext oldSubContext = subServiceCallStack.get().peek();
                ServiceCallContextHolder.setSubContext(oldSubContext);
            } else {
                ServiceCallContextHolder.clearSubContext();
                ServiceCallContextHolder.clearContext();
            }
        }

        return ret;
    }

    private boolean initGlobalContext(){
        boolean initialized = ServiceCallContextHolder.isContextInitialized();
        if (!initialized) {
            if (log.isDebugEnabled())
                log.debug("service call context gets initialized");
            ServiceCallContextHolder.setContext(serviceCallContextFactory.create());
        }
        return initialized;
    }

    private void initSubContext(){
        SubServiceCallContext subContext = new SubServiceCallContext();
        subServiceCallStack.get().push(subContext);
        ServiceCallContextHolder.setSubContext(subContext);
    }
}