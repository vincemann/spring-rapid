package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Proxy;
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
public class ServiceCallContextAdvice {

    IdConverter<?> idConverter;
    //    ThreadLocal<Stack<ServiceCallContext>> serviceCallStack = ThreadLocal.withInitial(Stack::new);
    ThreadLocal<Stack<Class<?>>> serviceCallStack = ThreadLocal.withInitial(Stack::new);

    public ServiceCallContextAdvice(IdConverter<?> idConverter) {
        this.idConverter = idConverter;
    }

    @Around(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() " +
            "&& com.github.vincemann.springrapid.core.advice.SystemArchitecture.ignoreExtensions()" +
            "&& com.github.vincemann.springrapid.core.advice.SystemArchitecture.ignoreHelperServiceMethods() "
    )
    public Object aroundServiceOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        System.err.println("SERVICE CONTEXT: " + joinPoint.getTarget().getClass().getSimpleName() + "->" + joinPoint.getSignature().getName());
//        Assert.isTrue(!(joinPoint.getTarget() instanceof AbstractServiceExtension));
//        Assert.isTrue(!(AopTestUtils.getUltimateTargetObject(joinPoint.getTarget()) instanceof AbstractServiceExtension));

        Object target = joinPoint.getTarget();

        if (target instanceof AbstractServiceExtension) {
            System.err.println("extension");
            return joinPoint.proceed();
        }

        ServiceCallContext context = ServiceCallContextHolder.createEmptyContext();


        Class<?> entityClass;
        if (Proxy.isProxyClass(target.getClass())) {
            entityClass = ProxyUtils.getExtensionProxy(((CrudService<?, ?>) target)).getLast().getEntityClass();
        } else {
            entityClass = ((CrudService) target).getEntityClass();
        }
        context.setCurrentEntityClass(entityClass);
        serviceCallStack.get().push(entityClass);
        ServiceCallContextHolder.setContext(context);



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
            // restore old, or clear if last
            serviceCallStack.get().pop();
            if (serviceCallStack.get().size() > 0) {
                Class<?> oldEntityClass = serviceCallStack.get().peek();
                ServiceCallContextHolder.getContext().setCurrentEntityClass(oldEntityClass);
            } else {
                ServiceCallContextHolder.clearContext();
            }
        }

        return ret;
    }
}
