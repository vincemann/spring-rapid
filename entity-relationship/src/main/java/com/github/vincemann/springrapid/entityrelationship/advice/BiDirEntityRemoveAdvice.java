package com.github.vincemann.springrapid.entityrelationship.advice;

import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Optional;

@Aspect
@Slf4j
//Transactional Method interceptor is called before this advice -> methods will be executed in repoTransaction
//@Order(100)
/**
 * Advice that keeps BiDirRelationships intact for {@link com.github.vincemann.springrapid.core.service.CrudService#deleteById(Serializable)} - operations.
 */
public class BiDirEntityRemoveAdvice {

    private CrudServiceLocator crudServiceLocator;


    @Autowired
    public BiDirEntityRemoveAdvice(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }


    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.deleteOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(id)")
    public void preRemoveBiDirEntity(JoinPoint joinPoint, Serializable id) throws Throwable {
        Optional<Object> parent = resolveById(id, joinPoint);
        if (parent.isPresent()){
            preRemoveEntity(parent.get());
        }else {
            log.warn("preDelete BiDirEntity could not be done, because for id: " + id + " was no entity found");
        }
    }


    private void preRemoveEntity(Object entity)  {
        if (BiDirParent.class.isAssignableFrom(entity.getClass())) {
            log.debug("applying pre remove BiDirParent logic for: " + entity.getClass());
            ((BiDirParent) entity).unlinkChildrensParent();
        }
        if (BiDirChild.class.isAssignableFrom(entity.getClass())) {
            log.debug("applying pre remove BiDirChild logic for: " + entity);
            BiDirChild biDirChild = (BiDirChild) entity;
            for (BiDirParent parent : biDirChild.findBiDirParents()) {
                parent.unlinkBiDirChild(biDirChild);
            }
            biDirChild.unlinkBiDirParents();
        }
    }

    private Optional<Object> resolveById(Serializable id, JoinPoint joinPoint) throws BadEntityException, IllegalAccessException {
        Class entityClass = resolveEntityClass(joinPoint);
        log.debug("pre remove hook reached for entity " + entityClass+":"+id);
        CrudService service = crudServiceLocator.find(entityClass);
        Assert.notNull(service,"Did not find service for entityClass: " + entityClass);
        return service.findById((id));
    }

    // todo change, is curreently impl specific
    private Class resolveEntityClass(JoinPoint joinPoint) throws IllegalAccessException {
        SimpleJpaRepository repo = AopTestUtils.getUltimateTargetObject(joinPoint.getTarget());
        Field entityInformationField = ReflectionUtils.findField(SimpleJpaRepository.class, field -> field.getName().equals("entityInformation"));
        entityInformationField.setAccessible(true);
        JpaEntityInformation entityInformation = ((JpaEntityInformation) entityInformationField.get(repo));
        return entityInformation.getJavaType();
    }

}
