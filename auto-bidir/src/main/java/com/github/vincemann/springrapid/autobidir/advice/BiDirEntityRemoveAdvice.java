package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import com.github.vincemann.springrapid.autobidir.model.RelationalEntityType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
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
import java.util.Set;

@Aspect
@Slf4j
//Transactional Method interceptor is called before this advice -> methods will be executed in repoTransaction
//@Order(100)
/**
 * Advice that keeps BiDirRelationships intact for {@link com.github.vincemann.springrapid.core.service.CrudService#deleteById(Serializable)} - operations.
 */
public class BiDirEntityRemoveAdvice extends BiDirEntityAdvice {


    @Autowired
    public BiDirEntityRemoveAdvice(CrudServiceLocator crudServiceLocator, RelationalEntityManager relationalEntityManager) {
        super(crudServiceLocator, relationalEntityManager);
    }

    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.deleteOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(id)")
    public void preRemoveBiDirEntity(JoinPoint joinPoint, Serializable id) throws Throwable {
        Optional<IdentifiableEntity> parent = resolveById(id, joinPoint);
        if (parent.isPresent()) {
            preRemoveEntity(parent.get());
        } else {
            log.warn("preDelete BiDirEntity could not be done, because for id: " + id + " was no entity found");
        }
    }


    private void preRemoveEntity(IdentifiableEntity entity) {
        Set<RelationalEntityType> relationalEntityTypes = relationalEntityManager.inferTypes(entity.getClass());

        if (relationalEntityTypes.contains(RelationalEntityType.BiDirParent)) {
            log.debug("applying pre remove BiDirParent logic for: " + entity.getClass());
            relationalEntityManager.unlinkChildrensParent(entity);
        }
        if (relationalEntityTypes.contains(RelationalEntityType.BiDirChild)) {
            log.debug("applying pre remove BiDirChild logic for: " + entity);
//            BiDirChild biDirChild = (BiDirChild) entity;
//            for (BiDirParent parent : biDirChild.findSingleBiDirParents()) {
//                parent.unlinkBiDirChild(biDirChild);
//            }
//            biDirChild.unlinkBiDirParents();
            relationalEntityManager.unlinkParentsChildren(entity);
        }
    }

    private Optional<IdentifiableEntity> resolveById(Serializable id, JoinPoint joinPoint) throws BadEntityException, IllegalAccessException {
        Class entityClass = resolveEntityClass(joinPoint);
        log.debug("pre remove hook reached for entity " + entityClass + ":" + id);
        CrudService service = getCrudServiceLocator().find(entityClass);
        Assert.notNull(service, "Did not find service for entityClass: " + entityClass);
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
