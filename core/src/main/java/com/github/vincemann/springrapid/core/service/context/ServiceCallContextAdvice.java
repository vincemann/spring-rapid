package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Stack;

/**
 * Sets {@link ServiceCallContext} in {@link ServiceCallContextHolder} for each service call.
 * hooks onto every service call for services implementing {@link CrudService}.
 * Lifetime of {@link ServiceCallContext} is the same as the most outer service call within the thread.
 * -> each Thread has own context
 *
 * context is created to allow thread wide caching especially between extensions within an extension chain.
 * i.E. extensions hook deleteById and all need to call findById(id) to operate on the entity -> multiple uncached findById calls
 *
 */
@Aspect
public class ServiceCallContextAdvice {

    IdConverter<?> idConverter;
//    ThreadLocal<Stack<ServiceCallContext>> serviceCallStack = ThreadLocal.withInitial(Stack::new);
    ThreadLocal<Stack<Class<?>>> serviceCallStack = ThreadLocal.withInitial(Stack::new);

    public ServiceCallContextAdvice(IdConverter<?> idConverter) {
        this.idConverter = idConverter;
    }

    @Around(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation()")
    public Object aroundServiceOperation(ProceedingJoinPoint joinPoint) throws Throwable {


        ServiceCallContext context = ServiceCallContextHolder.createEmptyContext();

        Class<?> entityClass = ((CrudService) joinPoint.getTarget()).getEntityClass();
        context.setCurrentEntityClass(entityClass);

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

        serviceCallStack.get().push(entityClass);
        ServiceCallContextHolder.setContext(context);

        Object ret;
        try {
           ret = joinPoint.proceed();
        }finally {
            // restore old, or clear if last
            serviceCallStack.get().pop();
            if (serviceCallStack.get().size() > 0){
                Class<?> oldEntityClass = serviceCallStack.get().peek();
                ServiceCallContextHolder.getContext().setCurrentEntityClass(oldEntityClass);
            } else{
                ServiceCallContextHolder.clearContext();
            }
        }

        return ret;
    }
}
