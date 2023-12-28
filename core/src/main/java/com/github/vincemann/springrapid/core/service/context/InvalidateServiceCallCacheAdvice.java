package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import java.io.Serializable;
import java.util.Set;

@Aspect
@Order(4)
@Slf4j
public class InvalidateServiceCallCacheAdvice {

    @Around(
            value = "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.fullUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity)")
    public Object aroundFullUpdateRelEntity(ProceedingJoinPoint joinPoint, IdentifiableEntity updateEntity) throws Throwable {
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return joinPoint.proceed();
        return updateCache(joinPoint, updateEntity.getId());
    }

    @Around(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.partialUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity,fieldsToRemove)")
    public Object aroundPartialUpdateRelEntity(ProceedingJoinPoint joinPoint, IdentifiableEntity updateEntity, String... fieldsToRemove) throws Throwable {
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return joinPoint.proceed();
        return updateCache(joinPoint, updateEntity.getId());
    }

    @Around(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.partialUpdateOperation() &&" +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity,propertiesToUpdate, fieldsToRemove)")
    public Object aroundPartialUpdateRelEntity(ProceedingJoinPoint joinPoint, IdentifiableEntity updateEntity, Set<String> propertiesToUpdate, String... fieldsToRemove) throws Throwable {
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return joinPoint.proceed();
        return updateCache(joinPoint, updateEntity.getId());
    }

    @Around(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() &&" +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.softUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity)")
    public Object aroundSoftUpdateRelEntity(ProceedingJoinPoint joinPoint, IdentifiableEntity updateEntity) throws Throwable {
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return joinPoint.proceed();
        return updateCache(joinPoint, updateEntity.getId());
    }

    @Around(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() &&" +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.deleteOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(id)")
    public Object aroundDeleteById(ProceedingJoinPoint joinPoint, Serializable id) throws Throwable {
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return joinPoint.proceed();
        return updateCache(joinPoint, id);
    }

    public Object updateCache(ProceedingJoinPoint joinPoint, Serializable id) throws Throwable {

//        System.err.println("CACHE FIND BY ID: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());
//        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
//            return joinPoint.proceed();

        Object retValue = joinPoint.proceed();
        // no exception thrown, so invalidate cache, if exception was thrown cache stays intact
        ServiceCallContext context = ServiceCallContextHolder.getContext();
        if (retValue != null){
            // update method -> update cache entry
            // we have new value to set to be cached
            if (log.isDebugEnabled())
                log.debug("updating cache with entity: " + retValue);
            context.setCachedEntity(((IdentifiableEntity) retValue));
        }else {
            // delete method -> invalidate
            Class entityClass = ((CrudService) joinPoint.getTarget()).getEntityClass();

            if (log.isDebugEnabled())
                log.debug("invalidating cache for: " + entityClass + "-" + id);

            context.removeCachedEntity(entityClass,id);
        }
        return retValue;
    }


}
