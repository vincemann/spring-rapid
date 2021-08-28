package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import com.github.vincemann.springrapid.autobidir.model.RelationalEntityType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.Set;

import static com.github.vincemann.springrapid.core.util.ProxyUtils.isRootService;

@Aspect
@Slf4j
@Transactional
/**
 * Advice that keeps BiDirRelationships intact for repo save operations that are updates (id is set)
 */
public class BiDirEntityUpdateAdvice extends BiDirEntityAdvice {


    @Autowired
    public BiDirEntityUpdateAdvice(CrudServiceLocator crudServiceLocator, RelationalEntityManager relationalEntityManager) {
        super(crudServiceLocator, relationalEntityManager);
    }


    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.updateOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(entity,full)")
    public void preUpdateBiDirEntity(JoinPoint joinPoint, IdentifiableEntity entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        try {
            if (!isRootService(joinPoint.getTarget())) {
                log.debug("ignoring service update advice, bc root service not called yet");
                return;
            }
            if ( entity.getId() != null && !full) {
                Set<RelationalEntityType> relationalEntityTypes = relationalEntityManager.inferTypes(entity.getClass());

                if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
                    log.debug("detected service partial update operation for BiDirParent: " + entity + ", running preUpdateAdvice logic");
                    updateBiDirParentRelations(entity);
                }
                if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
                    log.debug("detected service partial update operation for BiDirChild: " + entity + ", running preUpdateAdvice logic");
                    updateBiDirChildRelations(entity);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
