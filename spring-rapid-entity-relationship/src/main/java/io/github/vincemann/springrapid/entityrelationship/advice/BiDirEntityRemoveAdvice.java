package io.github.vincemann.springrapid.entityrelationship.advice;

import io.github.vincemann.springrapid.entityrelationship.model.biDir.child.BiDirChild;
import io.github.vincemann.springrapid.entityrelationship.model.biDir.parent.BiDirParent;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.exception.NoIdException;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Optional;

@Aspect
@Component
@Slf4j
//Transactional Method interceptor is called before this advice -> methods will be executed in repoTransaction
//@Order(100)
public class BiDirEntityRemoveAdvice /*implements MethodInterceptor*/ {

    private CrudServiceLocator crudServiceLocator;


    @Autowired
    public BiDirEntityRemoveAdvice(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }


    @Around("io.github.vincemann.springrapid.core.advice.SystemArchitecture.deleteOperation() && " +
            "io.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(serializable)")
    public Object preRemoveBiDirEntity(ProceedingJoinPoint joinPoint, Serializable serializable) throws Throwable {
        Optional<Object> parent = resolveId(serializable, joinPoint);
        if (parent.isPresent()){
            preRemoveEntity(parent.get());
        }else {
            log.warn("preDelete BiDirEntity could not be done, because for id: " + serializable + " was not entity found");
        }
        return joinPoint.proceed();
    }


    private void preRemoveEntity(Object entity) throws IllegalAccessException {
        if (BiDirParent.class.isAssignableFrom(entity.getClass())) {
            log.debug("applying pre remove BiDirParent logic for: " + entity.getClass());
            ((BiDirParent) entity).dismissChildrensParent();
        }
        if (BiDirChild.class.isAssignableFrom(entity.getClass())) {
            log.debug("applying pre remove BiDirChild logic for: " + entity);
            BiDirChild biDirChild = (BiDirChild) entity;
            for (BiDirParent parent : biDirChild.findParents()) {
                parent.dismissChild(biDirChild);
            }
            biDirChild.dismissParents();
        }
    }

    private Optional<Object> resolveId(Serializable id, ProceedingJoinPoint joinPoint) throws NoIdException, IllegalAccessException {
        Class entityClass = resolveEntityClass(joinPoint);
        log.debug("pre remove hook reached for entity " + entityClass+":"+id);
        CrudService service = crudServiceLocator.find(entityClass);
        Assert.notNull(service,"Did not find service for entityClass: " + entityClass);
        return service.findById((id));
    }

    private Class resolveEntityClass(ProceedingJoinPoint joinPoint) throws IllegalAccessException {
        SimpleJpaRepository jpaRepository = AopTestUtils.getUltimateTargetObject(joinPoint.getTarget());
        Field entityInformationField = ReflectionUtils.findField(SimpleJpaRepository.class, field -> field.getName().equals("entityInformation"));
        entityInformationField.setAccessible(true);
        JpaEntityInformation entityInformation = ((JpaEntityInformation) entityInformationField.get(jpaRepository));
        return entityInformation.getJavaType();
    }

}
