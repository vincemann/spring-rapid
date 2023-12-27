package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.autobidir.AutoBiDirUtils;
import com.github.vincemann.springrapid.autobidir.RelationalAdviceContext;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Optional;
import java.util.Set;

import static com.github.vincemann.springrapid.core.util.ProxyUtils.isRootService;

@Slf4j
@Aspect
@Order(2)
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
//           value = "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() " +
           "com.github.vincemann.springrapid.core.SystemArchitecture.fullUpdateOperation() && " +
//        "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
//        "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreProxies() && " +
                    "args(updateEntity)")
    public void preFullUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity) throws EntityNotFoundException {
        preBiDirEntity(joinPoint, updateEntity, RelationalAdviceContext.UpdateKind.FULL);
    }

    @Before(value =
//            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() &&" +
             "com.github.vincemann.springrapid.core.SystemArchitecture.partialUpdateOperation() && " +
//
//            "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
//            "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreProxies() && " +
            "args(updateEntity,fieldsToRemove)")
    public void prePartialUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity, String... fieldsToRemove) throws EntityNotFoundException {
        preBiDirEntity(joinPoint,updateEntity,RelationalAdviceContext.UpdateKind.PARTIAL);
    }

    @Before(value =
//            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() " +
            "com.github.vincemann.springrapid.core.SystemArchitecture.partialUpdateOperation() &&" +
//            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() && " +
//            "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
//            "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreProxies() && " +
            "args(updateEntity,propertiesToUpdate, fieldsToRemove)")
    public void prePartialUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity, Set<String> propertiesToUpdate, String... fieldsToRemove) throws EntityNotFoundException {
        preBiDirEntity(joinPoint,updateEntity,RelationalAdviceContext.UpdateKind.PARTIAL);
    }

    @Before(value =
//            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() &&" +
            "com.github.vincemann.springrapid.core.SystemArchitecture.softUpdateOperation() && " +
//            "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
//            "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreProxies() && " +
            "args(updateEntity)")
    public void preSoftUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity) throws EntityNotFoundException {
        preBiDirEntity(joinPoint, updateEntity, RelationalAdviceContext.UpdateKind.SOFT);
    }

    @Before(value =
//            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceOperation() &&" +
            "com.github.vincemann.springrapid.core.SystemArchitecture.serviceSaveOperation() && " +

//            "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreExtensions() && " +
//            "com.github.vincemann.springrapid.core.SystemArchitecture.ignoreProxies() && " +
            "args(createdEntity)")
    public void preCreateRelEntity(JoinPoint joinPoint, IdentifiableEntity createdEntity) throws EntityNotFoundException {
        preBiDirEntity(joinPoint, createdEntity, null);
    }


    // fields to remove not needed, already done via jpaCrudService.updates copyProperties call (removes those values)
    public void preBiDirEntity(JoinPoint joinPoint,  IdentifiableEntity entity, RelationalAdviceContext.UpdateKind updateKind) throws EntityNotFoundException {

        System.err.println("SETTING RELATIONAL CONTEXT: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());


        if (!isRootService(joinPoint.getTarget())) {
            return;
        }
        if (AutoBiDirUtils.isDisabled(joinPoint)){
            return;
        }

//        System.err.println("is root service");

        RelationalAdviceContext updateContext;
        if (updateKind == null){
            updateContext = RelationalAdviceContext.builder()
                    .updateKind(null)
                    .build();
        }
        else if (updateKind.equals(RelationalAdviceContext.UpdateKind.SOFT)){
             updateContext = RelationalAdviceContext.builder()
                    .updateKind(updateKind)
                    .build();
        }else {
            // java.lang.ClassCastException: class io.gitlab.vinceconrad.votesnackbackend.model.Exercise$HibernateProxy$ipV9X1Mb cannot be cast to class org.hibernate.proxy.LazyInitializer
            // -> need to unproxy as done further down below
//            IdentifiableEntity detachedOldEntity = BeanUtils.clone(entityLocator.findEntity(entity));
//            IdentifiableEntity detachedOldEntity =
//                    BeanUtils.clone(ProxyUtils.hibernateUnproxyRaw(
//                            entityLocator.findEntity(ProxyUtils.hibernateUnproxyRaw(entity))
//                    ));

            IdentifiableEntity unproxiedUpdateEntity = ProxyUtils.hibernateUnproxyRaw(entity);

            // used cloning here to fully detach entity by also detaching its collections - using util method for that now
            // can only detach collections by calling set = new HashSet(set);
            // somehow detaching even with util function is not enough
            // using cached resolve entity method here, if entity is modified, then setCacheDirty must be set!
            Optional<IdentifiableEntity> oldEntityOp = entityLocator.findEntity(unproxiedUpdateEntity);
            if (oldEntityOp.isEmpty())
                throw new EntityNotFoundException(unproxiedUpdateEntity.getId(),unproxiedUpdateEntity.getClass());
            IdentifiableEntity oldEntity = oldEntityOp.get();
            IdentifiableEntity<?> detachedOldEntity = BeanUtils.clone(ProxyUtils.hibernateUnproxyRaw(oldEntity));
//            IdentifiableEntity<?> detachedOldEntity = ProxyUtils.hibernateUnproxyRaw(byId.get());
            entityManager.detach(detachedOldEntity);
//            JpaUtils.detachCollections(detachedOldEntity);

            IdentifiableEntity detachedUpdateEntity = BeanUtils.clone(unproxiedUpdateEntity);
//            IdentifiableEntity detachedUpdateEntity = ProxyUtils.hibernateUnproxyRaw(entity);
            entityManager.detach(detachedUpdateEntity);
//            JpaUtils.detachCollections(detachedUpdateEntity);

            updateContext = RelationalAdviceContext.builder()
                    .detachedUpdateEntity(detachedUpdateEntity)
                    .detachedOldEntity(detachedOldEntity)
                    .updateKind(updateKind)
                    .build();
        }
        ServiceCallContextHolder.getSubContext().addValue(RELATIONAL_UPDATE_CONTEXT_KEY,updateContext);
//        RelationalAdviceContextHolder.setContext(updateContext);
    }
}
