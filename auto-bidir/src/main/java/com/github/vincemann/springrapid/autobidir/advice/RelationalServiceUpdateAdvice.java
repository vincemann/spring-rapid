package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.autobidir.RelationalAdviceContext;
import com.github.vincemann.springrapid.autobidir.RelationalAdviceContextHolder;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.BeanUtils;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.github.vincemann.springrapid.core.util.ProxyUtils.isRootService;

@Slf4j
@Aspect
public class RelationalServiceUpdateAdvice {

    @PersistenceContext
    private EntityManager entityManager;
    private EntityLocator entityLocator;

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.fullUpdateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(updateEntity)")
    public void preFullUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity) throws EntityNotFoundException, BadEntityException {
        preBiDirEntity(joinPoint, updateEntity, RelationalAdviceContext.UpdateKind.FULL);
    }

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.partialUpdateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(updateEntity,fieldsToRemove)")
    public void prePartialUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        preBiDirEntity(joinPoint,updateEntity,RelationalAdviceContext.UpdateKind.PARTIAL);
    }

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.softUpdateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(updateEntity)")
    public void preSoftUpdateRelEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity) throws EntityNotFoundException, BadEntityException {
        preBiDirEntity(joinPoint, updateEntity, RelationalAdviceContext.UpdateKind.SOFT);
    }

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(createdEntity)")
    public void preCreateRelEntity(JoinPoint joinPoint, IdentifiableEntity createdEntity) throws EntityNotFoundException, BadEntityException {
        preBiDirEntity(joinPoint, createdEntity, null);
    }



    public void preBiDirEntity(JoinPoint joinPoint,  IdentifiableEntity entity, RelationalAdviceContext.UpdateKind updateKind, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        if (!isRootService(joinPoint.getTarget())) {
            log.debug("ignoring service update advice, bc root service not called yet");
            return;
        }

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
//            IdentifiableEntity detachedOldEntity = BeanUtils.clone(entityLocator.findEntity(entity));
            IdentifiableEntity detachedOldEntity =
                    BeanUtils.clone(ProxyUtils.hibernateUnproxyRaw(
                            entityLocator.findEntity(ProxyUtils.hibernateUnproxyRaw(entity))
                    ));
            entityManager.detach(detachedOldEntity);

            IdentifiableEntity detachedUpdateEntity = BeanUtils.clone(ProxyUtils.hibernateUnproxyRaw(entity));
            entityManager.detach(detachedUpdateEntity);

            updateContext = RelationalAdviceContext.builder()
                    .detachedUpdateEntity(detachedUpdateEntity)
                    .detachedOldEntity(detachedOldEntity)
                    .updateKind(updateKind)
                    .build();
        }
        RelationalAdviceContextHolder.setContext(updateContext);
    }

    @Autowired
    public void setEntityLocator(EntityLocator entityLocator) {
        this.entityLocator = entityLocator;
    }
}
