package com.github.vincemann.springrapid.auth.service.context;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContext;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import java.io.Serializable;
import java.util.Optional;

@Aspect
@Order(3)
@Slf4j
public class AuthServiceCallCacheAdvice {

    @Around(value = "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() " +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions()" +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.findByIdOperation() " +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() " +
            "&& args(id)"
    )
    public Object cacheFindById(ProceedingJoinPoint joinPoint, Serializable id) throws Throwable {
//        System.err.println("AUTH SERVICE CALL CACHE: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return joinPoint.proceed();

        // is root service -> do caching
        Object target = joinPoint.getTarget();
        Class entityClass = ((CrudService) target).getEntityClass();
        ServiceCallContext context = ServiceCallContextHolder.getContext();
//        System.err.println("looking for: " + entityClass.getSimpleName() + ":" + id);
        Optional<Object> cached = context.getCachedEntity(entityClass, id);
        if (cached == null) {
//            System.err.println("not cached yet");
            Optional<? extends IdentifiableEntity<?>> value = (Optional<IdentifiableEntity<?>>) joinPoint.proceed();
//            System.err.println("adding value: " + value);
//            if (value.isPresent())
//                System.err.println("value in optional: " + value.get());
            context.setCachedEntity(entityClass, id, value);

            // make sure subsequent findByContactInformation calls will also find cached entry
            if (AbstractUser.class.isAssignableFrom(entityClass) && value.isPresent()){
                String contactInformation = ((AbstractUser) value.get()).getContactInformation();
                context.setCachedEntity(entityClass,contactInformation, value);
            }

            return value;
        } else {
            if (log.isDebugEnabled())
                log.debug("returning cached value: " + cached);
//            System.err.println("returning cached value: " + cached);
            return cached;
        }
    }

    @Around(value = "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() " +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions()" +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.findByIdOperation() " +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() " +
            "&& args(contactInformation)"
    )
    public Object cacheFindByContactInformation(ProceedingJoinPoint joinPoint, String contactInformation) throws Throwable {
//        System.err.println("AUTH SERVICE CALL CACHE" + joinPoint.getTarget().getClass().getSimpleName() + "->" + joinPoint.getSignature().getName());

        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return joinPoint.proceed();
        // is root service -> do caching
        Object target = joinPoint.getTarget();
        Class entityClass = ((CrudService) target).getEntityClass();
        ServiceCallContext context = ServiceCallContextHolder.getContext();
        Optional<Object> cached = context.getCachedEntity(entityClass, contactInformation);
        if (cached == null) {
            Optional<? extends IdentifiableEntity<?>> value = (Optional<IdentifiableEntity<?>>) joinPoint.proceed();
            context.setCachedEntity(entityClass, contactInformation, value);

            // make sure subsequent findById calls will also find cached entry
            if (AbstractUser.class.isAssignableFrom(entityClass) && value.isPresent()){
                Serializable id = ((AbstractUser) value.get()).getId();
                context.setCachedEntity(entityClass,id,value);
            }

            return value;
        } else {
            return cached;
        }
    }
}
