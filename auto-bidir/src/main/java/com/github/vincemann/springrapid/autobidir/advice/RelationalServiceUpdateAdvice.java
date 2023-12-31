package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.autobidir.AutoBiDirUtils;
import com.github.vincemann.springrapid.autobidir.RelationalAdviceContext;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Set;

@Slf4j
@Aspect
// order is very important
// influences how ofter advice is called for some reason
@Order(2)
public class RelationalServiceUpdateAdvice {

    public static final String RELATIONAL_UPDATE_CONTEXT_KEY = "relational-update-context";

    @Before(
            value = "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.fullUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity)")
    public void preFullUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity) throws EntityNotFoundException {
        System.err.println("full update matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return;
        }
        preBiDirEntity(joinPoint, updateEntity, RelationalAdviceContext.UpdateKind.FULL);
    }

    @Before(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.partialUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity,fieldsToRemove)")
    public void prePartialUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity, String... fieldsToRemove) throws EntityNotFoundException {
        System.err.println("partial update without propertiesToUpdate matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return;
        }
        preBiDirEntity(joinPoint, updateEntity, RelationalAdviceContext.UpdateKind.PARTIAL);
    }

    @Before(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.partialUpdateOperation() &&" +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity,propertiesToUpdate, fieldsToRemove)")
    public void prePartialUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity, Set<String> propertiesToUpdate, String... fieldsToRemove) throws EntityNotFoundException {
        System.err.println("partial update with propertiesToUpdate matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return;
        }
        preBiDirEntity(joinPoint, updateEntity, RelationalAdviceContext.UpdateKind.PARTIAL);
    }

    @Before(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() &&" +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.softUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity)")
    public void preSoftUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity) throws EntityNotFoundException {
        System.err.println("soft update matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return;
        }
        preBiDirEntity(joinPoint, updateEntity, RelationalAdviceContext.UpdateKind.SOFT);
    }

    @Before(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.saveOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(createdEntity)")
    public void preCreateRelEntity(JoinPoint joinPoint, IdentifiableEntity createdEntity) throws EntityNotFoundException {
        System.err.println("create matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return;
        }
        preBiDirEntity(joinPoint, createdEntity, null);
    }


    // fields to remove not needed, already done via jpaCrudService.updates copyProperties call (removes those values)
    public void preBiDirEntity(JoinPoint joinPoint, IdentifiableEntity entity, RelationalAdviceContext.UpdateKind updateKind) throws EntityNotFoundException {
        System.err.println("not ignoring: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());


        if (log.isDebugEnabled())
            log.debug("setting relational context: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        RelationalAdviceContext updateContext;
        if (updateKind == null) {
            updateContext = RelationalAdviceContext.builder()
                    .updateKind(null)
                    .build();
        } else if (updateKind.equals(RelationalAdviceContext.UpdateKind.SOFT)) {
            updateContext = RelationalAdviceContext.builder()
                    .updateKind(updateKind)
                    .build();
        } else {
            // java.lang.ClassCastException: class io.gitlab.vinceconrad.votesnackbackend.model.Exercise$HibernateProxy$ipV9X1Mb cannot be cast to class org.hibernate.proxy.LazyInitializer

            updateContext = RelationalAdviceContext.builder()
                    .detachedUpdateEntity(MyJpaUtils.deepDetachOrGet(entity))
                    .updateKind(updateKind)
                    .build();
        }
        ServiceCallContextHolder.getSubContext().setValue(RELATIONAL_UPDATE_CONTEXT_KEY, updateContext);
    }
}
