package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.autobidir.RelationalAdviceContext;
import com.github.vincemann.springrapid.autobidir.RelationalAdviceContextHolder;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.BeanUtils;
import com.github.vincemann.springrapid.core.util.EntityLocator;
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

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.updateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(updateEntity)")
    public void preUpdateBiDirEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity) throws EntityNotFoundException, BadEntityException {
        preUpdateBiDirEntity(joinPoint, updateEntity, true);
    }


    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.updateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(updateEntity,full)")
    public void preUpdateBiDirEntity(JoinPoint joinPoint, IdentifiableEntity updateEntity, Boolean full) throws EntityNotFoundException, BadEntityException {
        if (!isRootService(joinPoint.getTarget())) {
            log.debug("ignoring service update advice, bc root service not called yet");
            return;
        }
        IdentifiableEntity detachedOldEntity = BeanUtils.clone(entityLocator.findEntity(updateEntity));
        entityManager.detach(detachedOldEntity);

        IdentifiableEntity detachedUpdateEntity = BeanUtils.clone(updateEntity);
        entityManager.detach(detachedUpdateEntity);

        RelationalAdviceContext updateContext = RelationalAdviceContext.builder()
                .detachedUpdateEntity(detachedUpdateEntity)
                .detachedOldEntity(detachedOldEntity)
                .fullUpdate(full)
                .build();
        RelationalAdviceContextHolder.setContext(updateContext);
    }

    @Autowired
    public void setEntityLocator(EntityLocator entityLocator) {
        this.entityLocator = entityLocator;
    }
}
