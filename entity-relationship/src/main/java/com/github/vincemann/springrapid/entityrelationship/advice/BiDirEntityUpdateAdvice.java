package com.github.vincemann.springrapid.entityrelationship.advice;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.*;

import static com.github.vincemann.springrapid.core.util.ProxyUtils.isRootService;

@Aspect
@Slf4j
@Transactional
/**
 * Advice that keeps BiDirRelationships intact for service partial update operations
 * Only Works together with {@link BiDirEntitySaveAdvice}.
 */
public class BiDirEntityUpdateAdvice extends BiDirEntityAdvice{


    @Autowired
    public BiDirEntityUpdateAdvice(CrudServiceLocator crudServiceLocator) {
        super(crudServiceLocator);
    }

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.updateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(biDirChild,full)")
    public void preUpdateBiDirChild(JoinPoint joinPoint, BiDirChild biDirChild, Boolean full) throws EntityNotFoundException, BadEntityException {
        try {
            if (!isRootService(joinPoint.getTarget())) {
                log.debug("ignoring service update advice, bc root service not called yet");
                return;
            }

            if (((IdentifiableEntity) biDirChild).getId() != null && !full) {
                log.debug("detected service partial update operation for BiDirChild: " + biDirChild + ", running preUpdateAdvice logic");
                updateBiDirChildRelations(biDirChild);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.updateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(biDirParent,full)")
    public void preUpdateBiDirParent(JoinPoint joinPoint, BiDirParent biDirParent, Boolean full) throws EntityNotFoundException, BadEntityException {
        try {
            if (!isRootService(joinPoint.getTarget())) {
                log.debug("ignoring service update advice, bc root service not called yet");
                return;
            }
            if (((IdentifiableEntity) biDirParent).getId() != null && !full) {
                log.debug("detected service partial update operation for BiDirParent: " + biDirParent + ", running preUpdateAdvice logic");
                updateBiDirParentRelations(biDirParent);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
