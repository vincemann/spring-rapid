package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.autobidir.AutoBiDirUtils;
import com.github.vincemann.springrapid.autobidir.RelationalAdviceContext;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.*;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Aspect
@Order(2)
public class RelationalServiceUpdateAdvice {

    public static final String RELATIONAL_UPDATE_CONTEXT_KEY = "relational-update-context";

    @Autowired
    private EntityLocator entityLocator;

    @Before(
            value = "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.fullUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity)")
    public void preFullUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity) throws EntityNotFoundException {
//        System.err.println("full update matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return;
        }
        preFullUpdate(joinPoint,updateEntity);
    }

    @Before(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.partialUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity,fieldsToRemove)")
    public void prePartialUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity, String... fieldsToRemove) throws EntityNotFoundException {
//        System.err.println("partial update without propertiesToUpdate matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return;
        }
        prePartialUpdate(joinPoint,updateEntity,Sets.newHashSet(),fieldsToRemove);
    }

    @Before(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.partialUpdateOperation() &&" +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity,collectionsToUpdate, fieldsToRemove)")
    public void prePartialUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity, Set<String> collectionsToUpdate, String... fieldsToRemove) throws EntityNotFoundException {
//        System.err.println("partial update with collectionsToUpdate matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return;
        }
        prePartialUpdate(joinPoint,updateEntity,collectionsToUpdate,fieldsToRemove);
    }

    @Before(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() &&" +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.softUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity)")
    public void preSoftUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity) throws EntityNotFoundException {
//        System.err.println("soft update matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return;
        }
        preSoftUpdate();
    }

    @Before(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.saveOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(createdEntity)")
    public void preCreateRelEntity(JoinPoint joinPoint, IdentifiableEntity createdEntity) throws EntityNotFoundException {
//        System.err.println("create matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return;
        }
        RelationalAdviceContext updateContext = RelationalAdviceContext.builder()
                .operationType(RelationalAdviceContext.OperationType.CREATE)
                .build();
        ServiceCallContextHolder.getSubContext().setValue(RELATIONAL_UPDATE_CONTEXT_KEY, updateContext);
    }

    public void prePartialUpdate(JoinPoint joinPoint, IdentifiableEntity updateEntity, Set<String> collectionsToUpdate, String... fieldsToRemove){

        if (log.isDebugEnabled())
            log.debug("setting relational context: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());
        IdentifiableEntity old = entityLocator.findEntity(updateEntity).get();

        IdentifiableEntity detachedSourceEntity = ReflectionUtils.createInstance(updateEntity.getClass());
        Set<String> whiteList = new HashSet<>(collectionsToUpdate);
        whiteList.addAll(Arrays.asList(fieldsToRemove));
        // expects all collections to be initialized and not of Persistent Type
        NullAwareBeanUtils.copyProperties(detachedSourceEntity,old,whiteList);

        RelationalAdviceContext updateContext = RelationalAdviceContext.builder()
                .detachedUpdateEntity(MyJpaUtils.deepDetachOrGet(updateEntity))
                .detachedSourceEntity(detachedSourceEntity)
                .operationType(RelationalAdviceContext.OperationType.PARTIAL)
                .build();
        ServiceCallContextHolder.getSubContext().setValue(RELATIONAL_UPDATE_CONTEXT_KEY, updateContext);
    }

    public void preSoftUpdate(){
        RelationalAdviceContext updateContext = RelationalAdviceContext.builder()
                .operationType(RelationalAdviceContext.OperationType.SOFT)
                .build();
        ServiceCallContextHolder.getSubContext().setValue(RELATIONAL_UPDATE_CONTEXT_KEY, updateContext);
    }

    public void preFullUpdate(JoinPoint joinPoint, IdentifiableEntity updateEntity){
        // java.lang.ClassCastException: class io.gitlab.vinceconrad.votesnackbackend.model.Exercise$HibernateProxy$ipV9X1Mb cannot be cast to class org.hibernate.proxy.LazyInitializer

        IdentifiableEntity old = entityLocator.findEntity(updateEntity).get();
        IdentifiableEntity detachedSourceEntity = BeanUtils.clone(ProxyUtils.hibernateUnproxy(old));

        RelationalAdviceContext updateContext = RelationalAdviceContext.builder()
                .detachedUpdateEntity(MyJpaUtils.deepDetachOrGet(updateEntity))
                .detachedSourceEntity(detachedSourceEntity)
                .operationType(RelationalAdviceContext.OperationType.FULL)
                .build();
        ServiceCallContextHolder.getSubContext().setValue(RELATIONAL_UPDATE_CONTEXT_KEY, updateContext);
    }

}
