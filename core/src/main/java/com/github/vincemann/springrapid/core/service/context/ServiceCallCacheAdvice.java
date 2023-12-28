package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import java.io.Serializable;
import java.util.Optional;

@Aspect
@Order(3)
public class ServiceCallCacheAdvice {

    // todo add update methods that invalidate cache


    @Around(value = "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() " +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions()" +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.findByIdOperation() " +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreProxies() " +
            "&& args(id)"
    )
    public Object cacheFindById(ProceedingJoinPoint joinPoint, Serializable id) throws Throwable {

//        System.err.println("CACHE FIND BY ID: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return joinPoint.proceed();


        // is root service -> do caching
        Object target = joinPoint.getTarget();
        Class entityClass = ((CrudService) target).getEntityClass();
        ServiceCallContext context = ServiceCallContextHolder.getContext();
        Optional<Object> cached = context.getCachedEntity(entityClass, id);
        if (cached == null) {
            Optional<? extends IdentifiableEntity<?>> value = (Optional<IdentifiableEntity<?>>) joinPoint.proceed();
            context.addCachedEntity(entityClass, id, value);
            return value;
        } else {
            return cached;
        }
    }
}

