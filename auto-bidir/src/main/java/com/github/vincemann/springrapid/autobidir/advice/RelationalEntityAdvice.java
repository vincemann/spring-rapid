package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.autobidir.RelationalAdviceContext;
import com.github.vincemann.springrapid.autobidir.RelationalAdviceContextHolder;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

@Aspect
@Slf4j
public class RelationalEntityAdvice {


    private CrudServiceLocator crudServiceLocator;
    private RelationalEntityManager relationalEntityManager;

//    @PersistenceContext
//    private EntityManager entityManager;


    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.deleteOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(id)")
    public void preRemoveEntity(JoinPoint joinPoint, Serializable id) throws Throwable {
        Optional<IdentifiableEntity> entity = resolveById(joinPoint,id);
        if (entity.isPresent()) {
            relationalEntityManager.remove(entity.get());
        } else {
            log.warn("preDelete BiDirEntity could not be done, because for id: " + id + " was no entity found");
        }
    }


    @Before("com.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(entity)")
    public IdentifiableEntity prePersistEntity(JoinPoint joinPoint, IdentifiableEntity entity) throws Throwable {
        RelationalAdviceContext updateContext = RelationalAdviceContextHolder.getContext();
        if (entity.getId() == null || updateContext.getUpdateKind()==null) {
            // save
            relationalEntityManager.save(entity);
            RelationalAdviceContextHolder.clear();
//            return (IdentifiableEntity) joinPoint.proceed(new IdentifiableEntity[]{entity});
            return entity;
        } else {
            // update
            switch (updateContext.getUpdateKind()){
                case FULL:
                    relationalEntityManager.update(updateContext.getDetachedOldEntity(), entity);
                    RelationalAdviceContextHolder.clear();
                    break;
                case PARTIAL:
//                    relationalEntityManager.partialUpdate(updateContext.getDetachedOldEntity(), entity, updateContext.getDetachedUpdateEntity());
                    relationalEntityManager.partialUpdate(updateContext.getDetachedOldEntity(), ProxyUtils.hibernateUnproxyRaw(entity), updateContext.getDetachedUpdateEntity());
                    RelationalAdviceContextHolder.clear();
                    break;
                case SOFT:
                    RelationalAdviceContextHolder.clear();
                    break;
            }
            return entity;

//            entityManager.refresh(entity);
//            entity = entityManager.merge(entity);

//            boolean managed = entityManager.contains(entity);
//            if (!managed){
//                IdentifiableEntity merged = entityManager.merge(entity);
//                boolean managed2 = entityManager.contains(merged);
////                entityManager.persist(merged);
////                return merged;
//                return (IdentifiableEntity) joinPoint.proceed(new IdentifiableEntity[]{merged});
//            }
//            return (IdentifiableEntity) joinPoint.proceed(new IdentifiableEntity[]{entity});
        }
    }

//    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.updateOperation() && " +
//            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
//            "args(entity)")
//    public void preUpdateEntity(JoinPoint joinPoint, IdentifiableEntity entity) throws EntityNotFoundException, BadEntityException {
//        relationalEntityManager.update(entity,RelationalAdviceContext.isFullUpdate());
//        RelationalAdviceContext.clear();
//    }

    private Optional<IdentifiableEntity> resolveById(JoinPoint joinPoint, Serializable id) throws BadEntityException, IllegalAccessException {
        Class entityClass = resolveEntityClass(joinPoint);
//        log.debug("pre remove hook reached for entity " + entityClass + ":" + id);
        CrudService service = crudServiceLocator.find(entityClass);
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

    @Autowired
    public void setCrudServiceLocator(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }

    @Autowired
    public void setRelationalEntityManager(RelationalEntityManager relationalEntityManager) {
        this.relationalEntityManager = relationalEntityManager;
    }
}
