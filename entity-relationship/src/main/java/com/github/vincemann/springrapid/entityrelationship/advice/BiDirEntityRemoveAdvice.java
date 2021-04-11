package com.github.vincemann.springrapid.entityrelationship.advice;

import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Optional;

@Aspect
@Slf4j
//Transactional Method interceptor is called before this advice -> methods will be executed in repoTransaction
//@Order(100)
/**
 * Advice that keeps BiDirRelationships intact for {@link com.github.vincemann.springrapid.core.service.CrudService#deleteById(Serializable)} - operations.
 */
public class BiDirEntityRemoveAdvice /*implements MethodInterceptor*/ {

    private CrudServiceLocator crudServiceLocator;


    @Autowired
    public BiDirEntityRemoveAdvice(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }


    @Around("com.github.vincemann.springrapid.core.advice.SystemArchitecture.deleteOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.serviceOperation() && " +
            "args(id)")
    public Object preRemoveBiDirEntity(ProceedingJoinPoint joinPoint, Serializable id) throws Throwable {
        Optional<Object> parent = resolveId(id, joinPoint);
        if (parent.isPresent()){
            preRemoveEntity(parent.get());
        }else {
            log.warn("preDelete BiDirEntity could not be done, because for id: " + id + " was not entity found");
        }
        return joinPoint.proceed();
    }


    private void preRemoveEntity(Object entity)  {
        if (BiDirParent.class.isAssignableFrom(entity.getClass())) {
            log.debug("applying pre remove BiDirParent logic for: " + entity.getClass());
            ((BiDirParent) entity).dismissChildrensParent();
        }
        if (BiDirChild.class.isAssignableFrom(entity.getClass())) {
            log.debug("applying pre remove BiDirChild logic for: " + entity);
            BiDirChild biDirChild = (BiDirChild) entity;
            for (BiDirParent parent : biDirChild.findBiDirParents()) {
                parent.dismissBiDirChild(biDirChild);
            }
            biDirChild.dismissBiDirParents();
        }
    }

    private Optional<Object> resolveId(Serializable id, ProceedingJoinPoint joinPoint) throws BadEntityException, IllegalAccessException {
        Class entityClass = resolveEntityClass(joinPoint);
        log.debug("pre remove hook reached for entity " + entityClass+":"+id);
        CrudService service = crudServiceLocator.find(entityClass);
        Assert.notNull(service,"Did not find service for entityClass: " + entityClass);
        return service.findById((id));
    }

    private Class resolveEntityClass(ProceedingJoinPoint joinPoint) throws IllegalAccessException {
        CrudService crudService = AopTestUtils.getUltimateTargetObject(joinPoint.getTarget());
//        Field entityInformationField = ReflectionUtils.findField(SimpleJpaRepository.class, field -> field.getName().equals("entityInformation"));
//        entityInformationField.setAccessible(true);
//        JpaEntityInformation entityInformation = ((JpaEntityInformation) entityInformationField.get(crudService));
//        return entityInformation.getJavaType();
        return crudService.getEntityClass();
    }

}
