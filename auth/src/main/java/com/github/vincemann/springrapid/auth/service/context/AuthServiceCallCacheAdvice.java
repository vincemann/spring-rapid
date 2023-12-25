package com.github.vincemann.springrapid.auth.service.context;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContext;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import java.io.Serializable;
import java.util.Optional;

@Aspect
@Order(3)
public class AuthServiceCallCacheAdvice {

    @Around(value = "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() " +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions()" +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.findByIdOperation() " +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreProxies() " +
            "&& args(id)"
    )
    public Object cacheFindById(ProceedingJoinPoint joinPoint, Serializable id) throws Throwable {
        System.err.println("AUTH SERVICE CALL CACHE: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (ProxyUtils.isJDKProxy(joinPoint.getTarget()) || joinPoint.getTarget() instanceof AbstractServiceExtension)
            return joinPoint.proceed();
        // is root service -> do caching
        Object target = joinPoint.getTarget();
        Class entityClass = ((CrudService) target).getEntityClass();
        ServiceCallContext context = ServiceCallContextHolder.getContext();
        Optional<Object> cached = context.getCachedEntity(entityClass, id);
        if (cached == null) {
            Optional<? extends IdentifiableEntity<?>> value = (Optional<IdentifiableEntity<?>>) joinPoint.proceed();
            context.addCachedEntity(entityClass, id, value);

            // make sure subsequent findByContactInformation calls will also find cached entry
            if (AbstractUser.class.isAssignableFrom(entityClass) && value.isPresent()){
                String contactInformation = ((AbstractUser) value.get()).getContactInformation();
                context.addCachedEntity(entityClass,contactInformation, value);
            }

            return value;
        } else {
            return cached;
        }
    }

    @Around(value = "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() " +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions()" +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.findByIdOperation() " +
            "&& com.github.vincemann.springrapid.core.SystemArchitecture.ignoreProxies() " +
            "&& args(contactInformation)"
    )
    public Object cacheFindByContactInformation(ProceedingJoinPoint joinPoint, String contactInformation) throws Throwable {
        System.err.println("AUTH SERVICE CALL CACHE" + joinPoint.getTarget().getClass().getSimpleName() + "->" + joinPoint.getSignature().getName());

        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return joinPoint.proceed();
        // is root service -> do caching
        Object target = joinPoint.getTarget();
        Class entityClass = ((CrudService) target).getEntityClass();
        ServiceCallContext context = ServiceCallContextHolder.getContext();
        Optional<Object> cached = context.getCachedEntity(entityClass, contactInformation);
        if (cached == null) {
            Optional<? extends IdentifiableEntity<?>> value = (Optional<IdentifiableEntity<?>>) joinPoint.proceed();
            context.addCachedEntity(entityClass, contactInformation, value);

            // make sure subsequent findById calls will also find cached entry
            if (AbstractUser.class.isAssignableFrom(entityClass) && value.isPresent()){
                Serializable id = ((AbstractUser) value.get()).getId();
                context.addCachedEntity(entityClass,id,value);
            }

            return value;
        } else {
            return cached;
        }
    }
}
