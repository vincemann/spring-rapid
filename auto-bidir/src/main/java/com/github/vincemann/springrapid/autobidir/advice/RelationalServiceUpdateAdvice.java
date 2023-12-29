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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Set;

import static com.github.vincemann.springrapid.core.util.ProxyUtils.getTargetClass;

@Slf4j
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
public class RelationalServiceUpdateAdvice {

    public static final String RELATIONAL_UPDATE_CONTEXT_KEY = "relational-update-context";

    @PersistenceContext
    private EntityManager entityManager;


    private EntityLocator entityLocator;

    @Autowired
    public void setEntityLocator(EntityLocator entityLocator) {
        this.entityLocator = entityLocator;
    }

    @Before(
            value = "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.fullUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreJdkProxies() && " +
                    "args(updateEntity)")
    public void preFullUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity) throws EntityNotFoundException {
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
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return;
        }
        preBiDirEntity(joinPoint, createdEntity, null);
    }


    // fields to remove not needed, already done via jpaCrudService.updates copyProperties call (removes those values)
    public void preBiDirEntity(JoinPoint joinPoint, IdentifiableEntity entity, RelationalAdviceContext.UpdateKind updateKind) throws EntityNotFoundException {

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
            // -> need to unproxy as done further down below
//            IdentifiableEntity detachedOldEntity = BeanUtils.clone(entityLocator.findEntity(entity));
//            IdentifiableEntity detachedOldEntity =
//                    BeanUtils.clone(ProxyUtils.hibernateUnproxyRaw(
//                            entityLocator.findEntity(ProxyUtils.hibernateUnproxyRaw(entity))
//                    ));


            // used cloning here to fully detach entity by also detaching its collections - using util method for that now
            // can only detach collections by calling set = new HashSet(set);
            // somehow detaching even with util function is not enough
            // using cached resolve entity method here, if entity is modified, then setCacheDirty must be set!

            // what if I never detached the entity fully via clone, just set the entity with its persistentSets to context
            // then further downstream just merge the entity back to the session

            // IdentifiableEntity unproxiedUpdateEntity = ProxyUtils.hibernateUnproxyRaw(entity);

            // PARTIAL
            // todo reverse this: fully detach update entity (not so costly) and then operate on the oldEntity that was not initialized lazily update everything copied from detached update entity
            // fully detach update entity (should already be detached tho, but maybe someone has set a PersistentSet on accident)
            // copy values from update entity to old/managed entity somehow with setters, but only do with primitive values -> those updates are done immediatly via hibernate proxy
            // then deal with all subEntities completely in advice downstream
            // entity added: got entity to add from update - merge the entity, will be detached
            // entity removed: no need for any merges I think
            IdentifiableEntity oldEntity = VerifyEntity.isPresent(
                    entityLocator.findEntity(getTargetClass(entity),entity.getId()),entity.getId(),getTargetClass(entity));
            oldEntity = BeanUtils.clone(oldEntity);

            // FULL
            // is like save, but with extensive look what has changed idk
            // i need the old state
            // maybe its very similar to partialUpdate
            // fully detach update entity (I need one detached version to compare to, so it doesnt get less expensive than this)
            // add to docs, that full update should be avoided bc costly and also add the detach requirements
            // just copy everything over with setters, then do the usual check also performed in partial update, but without limited properties restrictions

//            IdentifiableEntity<?> detachedOldEntity = oldEntity;
//            IdentifiableEntity<?> detachedOldEntity = BeanUtils.clone(oldEntity);
//            IdentifiableEntity<?> detachedOldEntity = ProxyUtils.hibernateUnproxyRaw(byId.get());
//            entityManager.detach(detachedOldEntity);
//            JpaUtils.detachCollections(detachedOldEntity);

//            IdentifiableEntity update;
//            if (updateKind.equals(RelationalAdviceContext.UpdateKind.PARTIAL)){
//                // need full detachment of partial entity, which isnt very costly when doing partial update
//                update = BeanUtils.clone(entity);
//            } else{
//                // dont need to detach update entity, when full update
//                update = entity;
//            }
//            IdentifiableEntity detachedUpdateEntity = entity;
//            IdentifiableEntity detachedUpdateEntity = ProxyUtils.hibernateUnproxyRaw(entity);
//            entityManager.detach(detachedUpdateEntity);
//            JpaUtils.detachCollections(detachedUpdateEntity);

            updateContext = RelationalAdviceContext.builder()
//                    .updateEntity(update)
                    .oldEntity(oldEntity)
                    .updateKind(updateKind)
                    .build();
        }
        ServiceCallContextHolder.getSubContext().setValue(RELATIONAL_UPDATE_CONTEXT_KEY, updateContext);
//        RelationalAdviceContextHolder.setContext(updateContext);
    }
}
