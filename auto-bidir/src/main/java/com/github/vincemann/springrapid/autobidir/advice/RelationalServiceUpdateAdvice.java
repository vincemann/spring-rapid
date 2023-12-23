package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.autobidir.AutoBiDirUtils;
import com.github.vincemann.springrapid.autobidir.RelationalAdviceContext;
import com.github.vincemann.springrapid.autobidir.RelationalAdviceContextHolder;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Optional;
import java.util.Set;

import static com.github.vincemann.springrapid.core.util.ProxyUtils.isRootService;

@Slf4j
@Aspect
public class RelationalServiceUpdateAdvice {

    @PersistenceContext
    private EntityManager entityManager;
//    private EntityLocator entityLocator;

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.fullUpdateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.ignoreExtensions() && " +
            "args(updateEntity)")
    public void preFullUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity) throws EntityNotFoundException, BadEntityException {
        preBiDirEntity(joinPoint, updateEntity, RelationalAdviceContext.UpdateKind.FULL);
    }

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.partialUpdateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.ignoreExtensions() && " +
            "args(updateEntity,fieldsToRemove)")
    public void prePartialUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        preBiDirEntity(joinPoint,updateEntity,RelationalAdviceContext.UpdateKind.PARTIAL);
    }

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.partialUpdateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.ignoreExtensions() && " +
            "args(updateEntity,propertiesToUpdate, fieldsToRemove)")
    public void prePartialUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity, Set<String> propertiesToUpdate, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        preBiDirEntity(joinPoint,updateEntity,RelationalAdviceContext.UpdateKind.PARTIAL);
    }

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.softUpdateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.ignoreExtensions() && " +
            "args(updateEntity)")
    public void preSoftUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity) throws EntityNotFoundException {
        preBiDirEntity(joinPoint, updateEntity, RelationalAdviceContext.UpdateKind.SOFT);
    }

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.ignoreExtensions() && " +
            "args(createdEntity)")
    public void preCreateRelEntity(JoinPoint joinPoint, IdentifiableEntity createdEntity) throws EntityNotFoundException {
        preBiDirEntity(joinPoint, createdEntity, null);
    }


    // fields to remove not needed, already done via jpaCrudService.updates copyProperties call (removes those values)
    public void preBiDirEntity(JoinPoint joinPoint,  IdentifiableEntity entity, RelationalAdviceContext.UpdateKind updateKind) throws EntityNotFoundException {
//        System.err.println("SERVICE UPDATE ADVICE: " + joinPoint.getTarget().getClass().getSimpleName() + "->" + joinPoint.getSignature().getName());
//        Assert.isTrue(!(joinPoint.getTarget() instanceof AbstractServiceExtension));
//        Assert.isTrue(!(AopTestUtils.getUltimateTargetObject(joinPoint.getTarget()) instanceof AbstractServiceExtension));

        if (!isRootService(joinPoint.getTarget())) {
            if (log.isDebugEnabled()){
//                System.err.println("not root service");
                log.debug("not root service");
            }
//                log.debug("ignoring service update advice, bc root service not called yet");
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


            // used cloning here to fully detach entity by also detaching its collections - using util method for that now
            // can only detach collections by calling set = new HashSet(set);
            // somehow detaching even with util function is not enough
            Optional<IdentifiableEntity> byId = EntityLocator.findEntity(ProxyUtils.hibernateUnproxyRaw(entity));
            VerifyEntity.isPresent(byId,entity.getId(),entity.getClass());
            IdentifiableEntity<?> detachedOldEntity = BeanUtils.clone(ProxyUtils.hibernateUnproxyRaw(byId.get()));
//            IdentifiableEntity<?> detachedOldEntity = ProxyUtils.hibernateUnproxyRaw(byId.get());
            entityManager.detach(detachedOldEntity);
//            JpaUtils.detachCollections(detachedOldEntity);

            IdentifiableEntity detachedUpdateEntity = BeanUtils.clone(ProxyUtils.hibernateUnproxyRaw(entity));
//            IdentifiableEntity detachedUpdateEntity = ProxyUtils.hibernateUnproxyRaw(entity);
            entityManager.detach(detachedUpdateEntity);
//            JpaUtils.detachCollections(detachedUpdateEntity);

            updateContext = RelationalAdviceContext.builder()
                    .detachedUpdateEntity(detachedUpdateEntity)
                    .detachedOldEntity(detachedOldEntity)
                    .updateKind(updateKind)
                    .build();
        }
        RelationalAdviceContextHolder.setContext(updateContext);
    }

//    @Autowired
//    public void setEntityLocator(EntityLocator entityLocator) {
//        this.entityLocator = entityLocator;
//    }
}
