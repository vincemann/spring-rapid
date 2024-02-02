package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.MyJpaUtils;
import com.github.vincemann.springrapid.core.util.NullAwareBeanUtils;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import com.github.vincemann.springrapid.core.util.ReflectionUtils;
import com.github.vincemann.springrapid.sync.repo.AuditLogRepository;
import com.github.vincemann.springrapid.sync.repo.EntityDtoMappingRepository;
import com.google.common.collect.Sets;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Aspect
public class AuditAdvice {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private EntityDtoMappingRepository entityDtoMappingRepository;


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
        // java.lang.ClassCastException: class io.gitlab.vinceconrad.votesnackbackend.model.Exercise$HibernateProxy$ipV9X1Mb cannot be cast to class org.hibernate.proxy.LazyInitializer
        // -> use unproxy in jpaUtils

        IdentifiableEntity old = entityLocator.findEntity(updateEntity).get();
        IdentifiableEntity detachedOldEntity = MyJpaUtils.deepDetach(old);

        RelationalAdviceContext updateContext = RelationalAdviceContext.builder()
                .detachedUpdateEntity(MyJpaUtils.deepDetachOrGet(updateEntity))
                .detachedOldEntity(detachedOldEntity)
                .operationType(RelationalAdviceContext.OperationType.FULL)
                .build();
        ServiceCallContextHolder.getSubContext().setValue(RELATIONAL_UPDATE_CONTEXT_KEY, updateContext);
    }

    @Before(value =
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.partialUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity,fieldsToUpdate)")
    public void prePartialUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity, String... fieldsToUpdate) throws EntityNotFoundException {
//        System.err.println("partial update without propertiesToUpdate matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return;
        }
        prePartialUpdate(joinPoint,updateEntity,fieldsToUpdate);
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
        RelationalAdviceContext updateContext = RelationalAdviceContext.builder()
                .operationType(RelationalAdviceContext.OperationType.SOFT)
                .build();
        ServiceCallContextHolder.getSubContext().setValue(RELATIONAL_UPDATE_CONTEXT_KEY, updateContext);
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

    public void prePartialUpdate(JoinPoint joinPoint, IdentifiableEntity updateEntity, String... fieldsToUpdate){

        if (log.isDebugEnabled())
            log.debug("setting relational context: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());
        IdentifiableEntity old = entityLocator.findEntity(updateEntity).get();

        Set<String> whiteList;
        if (fieldsToUpdate.length == 0)
            whiteList =  ReflectionUtils.findAllNonNullFieldNames(updateEntity);
        else
            whiteList = Sets.newHashSet(fieldsToUpdate);

        // expects all collections to be initialized and not of Persistent Type
        IdentifiableEntity detachedOldEntity = ReflectionUtils.createInstance(ProxyUtils.getTargetClass(updateEntity));
        NullAwareBeanUtils.copyProperties(detachedOldEntity,old,whiteList);

        RelationalAdviceContext updateContext = RelationalAdviceContext.builder()
                .detachedUpdateEntity(MyJpaUtils.deepDetachOrGet(updateEntity))
                .detachedOldEntity(detachedOldEntity)
                .whiteListedFields(whiteList)
                .operationType(RelationalAdviceContext.OperationType.PARTIAL)
                .build();
        ServiceCallContextHolder.getSubContext().setValue(RELATIONAL_UPDATE_CONTEXT_KEY, updateContext);
    }





}
