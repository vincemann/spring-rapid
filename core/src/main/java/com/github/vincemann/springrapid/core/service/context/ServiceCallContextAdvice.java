package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
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

//    IdConverter<?> idConverter;
    //    ThreadLocal<Stack<ServiceCallContext>> serviceCallStack = ThreadLocal.withInitial(Stack::new);
//    ThreadLocal<Stack<Class<?>>> serviceCallStack = ThreadLocal.withInitial(Stack::new);
    ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);
//    ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);

//    public ServiceCallContextAdvice(IdConverter<?> idConverter) {
//        this.idConverter = idConverter;
//    }

    private ServiceCallContextFactory serviceCallContextFactory;

    @Autowired
    public void setServiceCallContextFactory(ServiceCallContextFactory serviceCallContextFactory) {
        this.serviceCallContextFactory = serviceCallContextFactory;
    }

    @Around(value = "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() " +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions()" +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreHelperServiceMethods() "
    )
    public Object aroundServiceOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        System.err.println("SERVICE CALL CONTEXT: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());
//        Assert.isTrue(!(joinPoint.getTarget() instanceof AbstractServiceExtension));
//        Assert.isTrue(!(AopTestUtils.getUltimateTargetObject(joinPoint.getTarget()) instanceof AbstractServiceExtension));

        Object target = joinPoint.getTarget();

        if (target instanceof AbstractServiceExtension)
            return joinPoint.proceed();

        if (!ServiceCallContextHolder.isInitialized()){
            log.debug("service call context gets initialized");
            ServiceCallContextHolder.setContext(serviceCallContextFactory.create());
        }

        depth.set(depth.get()+1);

//        if (!ProxyUtils.isJDKProxy(target)){
//            // is root service
//            Class<?> entityClass = ((CrudService) target).getEntityClass();
//        }








//        ServiceCallContext context = ServiceCallContextHolder.createEmptyContext();



//        context.setCurrentEntityClass(entityClass);
//        serviceCallStack.get().push(entityClass);
//        ServiceCallContextHolder.setContext(context);

//        if (!ServiceCallContextHolder.isInitialized()){
//            log.debug("service call context gets initialized");
//            ServiceCallContextHolder.setContext(serviceCallContextFactory.create());
//        }
////        ServiceCallContextHolder.getContext().setCurrentEntityClass(entityClass);
//        depth.set(depth.get()+1);



//        Object[] args = joinPoint.getArgs();
//        if (args.length > 0){
//            Object firstArg = args[0];
//            if (firstArg != null){
//
//                if (firstArg instanceof IdentifiableEntity){
//                    context.setId(((IdentifiableEntity<?>) firstArg).getId());
//                }else if (idConverter.getIdType().equals(firstArg.getClass())){
//                    context.setId(idConverter.toId(String.valueOf(firstArg)));
//                }
//            }
//        }

        Object ret;
        try {
            ret = joinPoint.proceed();
        } finally {
//            // restore old, or clear if last
//            depth.get().pop();
//            if (depth.get().size() > 0) {
//                Class<?> oldEntityClass = depth.get().peek();
//                ServiceCallContextHolder.getContext().setCurrentEntityClass(oldEntityClass);
//            } else {
//                ServiceCallContextHolder.clearContext();
//            }
            decrementDepth();
        }
//        decrementDepth();

        return ret;
    }

    private void decrementDepth(){
        depth.set(depth.get()-1);
        if (depth.get() == 0)
            ServiceCallContextHolder.clearContext();
    }
}