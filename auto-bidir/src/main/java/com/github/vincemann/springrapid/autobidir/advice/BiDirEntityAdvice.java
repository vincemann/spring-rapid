package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.autobidir.AutoBiDirUtils;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.*;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

@Aspect
@Slf4j
@Order(4)
public class BiDirEntityAdvice {

    private RelationalEntityManager relationalEntityManager;

    private CrudServiceLocator crudServiceLocator;


    @Before(
            value = "com.github.vincemann.springrapid.core.RapidArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.deleteOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreJdkProxies() && " +
                    "args(id)")
    public void beforeDeleteById(JoinPoint joinPoint, Serializable id) throws EntityNotFoundException, BadEntityException {
        //        System.err.println("delete matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (skip(joinPoint))
            return;

        Optional<IdentifiableEntity> entity = findById(joinPoint, id);
        if (entity.isPresent()) {
            relationalEntityManager.delete(entity.get());
        } else {
            log.warn("preDelete BiDirEntity could not be done, because for id: " + id + " was no entity found");
        }
    }

    @Before(
            value = "com.github.vincemann.springrapid.core.RapidArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.fullUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreJdkProxies() && " +
                    "args(update)")
    public void beforeFullUpdate(JoinPoint joinPoint, IdentifiableEntity update) throws EntityNotFoundException, BadEntityException {
//        System.err.println("full update matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (skip(joinPoint))
            return;

        IdentifiableEntity old = findById(joinPoint, update.getId()).get();
        IdentifiableEntity detachedOldEntity = JpaUtils.deepDetach(old);

        relationalEntityManager.fullUpdate(old, detachedOldEntity, update);
    }

    @Before(
            value = "com.github.vincemann.springrapid.core.RapidArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.partialUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreJdkProxies() && " +
                    "args(update,fieldsToUpdate)")
    public void beforePartialUpdate(JoinPoint joinPoint, IdentifiableEntity update, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException {
//        System.err.println("partial update without propertiesToUpdate matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (skip(joinPoint))
            return;

        IdentifiableEntity old = findById(joinPoint, update.getId()).get();

        Set<String> _fieldsToUpdate;
        if (fieldsToUpdate.length == 0)
            _fieldsToUpdate = ReflectionUtils.findAllNonNullFieldNames(update);
        else
            _fieldsToUpdate = Sets.newHashSet(fieldsToUpdate);

        // expects all collections to be initialized and not of Persistent Type
        IdentifiableEntity detachedOldEntity = ReflectionUtils.createInstance(ProxyUtils.getTargetClass(update));
        NullAwareBeanUtils.copyProperties(detachedOldEntity, old, _fieldsToUpdate);

        relationalEntityManager.partialUpdate(old, detachedOldEntity, update, _fieldsToUpdate.toArray(new String[0]));
    }

    @Before(
            value = "com.github.vincemann.springrapid.core.RapidArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.saveOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreJdkProxies() && " +
                    "args(entity)")
    public void beforeCreate(JoinPoint joinPoint, IdentifiableEntity entity) {
//        System.err.println("create matches " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());
        if (skip(joinPoint))
            return;

        relationalEntityManager.save(entity);
    }

    protected boolean skip(JoinPoint joinPoint) {
        // the ignore pointcuts sometimes dont work as expected
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return true;
        if (AutoBiDirUtils.isDisabled(joinPoint)) {
            return true;
        }
        return false;
    }

    protected Optional<IdentifiableEntity> findById(JoinPoint joinPoint, Serializable id) {
        CrudService service = AopTestUtils.getUltimateTargetObject(joinPoint.getTarget());
        // go via crud service locator so aop is not stripped off
        return crudServiceLocator.find(service.getEntityClass()).findById(id);
    }


    @Autowired
    public void setRelationalEntityManager(RelationalEntityManager relationalEntityManager) {
        this.relationalEntityManager = relationalEntityManager;
    }

    @Autowired
    public void setCrudServiceLocator(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }
}
