package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.autobidir.AutoBiDirUtils;
import com.github.vincemann.springrapid.autobidir.RelationalAdviceContext;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.service.context.SubServiceCallContext;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.util.AopTestUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Optional;

import static com.github.vincemann.springrapid.autobidir.advice.RelationalServiceUpdateAdvice.RELATIONAL_UPDATE_CONTEXT_KEY;

@Aspect
@Slf4j
public class RelationalEntityAdvice {

    private EntityLocator entityLocator;
    private RelationalEntityManager relationalEntityManager;

    private CrudServiceLocator crudServiceLocator;

    @PersistenceContext
    private EntityManager entityManager;


    @Before("com.github.vincemann.springrapid.core.SystemArchitecture.deleteOperation() && " +
            "com.github.vincemann.springrapid.core.SystemArchitecture.repoOperation() && " +
            "args(id)")
    public void preRemoveEntity(JoinPoint joinPoint, Serializable id) throws Throwable {

//        System.err.println("PRE REMOVE: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (AutoBiDirUtils.isDisabled(joinPoint)){
            return;
        }
        Optional<IdentifiableEntity> entity = resolveById(joinPoint,id);
        if (entity.isPresent()) {
            relationalEntityManager.remove(entity.get());
        } else {
            log.warn("preDelete BiDirEntity could not be done, because for id: " + id + " was no entity found");
        }
    }


    @Before("com.github.vincemann.springrapid.core.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.SystemArchitecture.repoOperation() && " +
            "args(entity)")
    public IdentifiableEntity prePersistEntity(JoinPoint joinPoint, IdentifiableEntity entity) throws Throwable {

//        System.err.println("PRE PERSIST: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (AutoBiDirUtils.isDisabled(joinPoint)){
            return entity;
        }


        RelationalAdviceContext updateContext = null;
        if (ServiceCallContextHolder.getSubContext() != null)
            updateContext = ServiceCallContextHolder.getSubContext().getValue(RELATIONAL_UPDATE_CONTEXT_KEY);


        if (updateContext == null){
            if (log.isWarnEnabled())
                log.warn("update context is null - only limited auto-rel management possible");
            // repo is called directly, not from service via update- or save-methods
            if (entity.getId() == null){
                // save operation and update context null
                relationalEntityManager.save(entity);
                clearContext();
                return entity;
            }else{
                // update context null and no safe operation
                if (log.isWarnEnabled())
                    log.warn("update context null and update operation - assuming full update");
//                throw new IllegalArgumentException("Cannot use update function yet without calling service upfront");

//                // repo is called directly, so also use repo to find old entity
                Optional<IdentifiableEntity> old = repoResolveById(joinPoint, entity.getId());
                VerifyEntity.isPresent(old,entity.getId(),entity.getClass());
                // full detach only works like that
                IdentifiableEntity oldEntity = BeanUtils.clone(ProxyUtils.hibernateUnproxyRaw(old.get()));
                entityManager.detach(oldEntity);
                relationalEntityManager.update(oldEntity, entity);
                clearContext();
                return entity;
            }
        }

        // update context is not null

        if (entity.getId() == null || updateContext.getUpdateKind()==null) {
            // save
            relationalEntityManager.save(entity);
            clearContext();
            return entity;
        } else {
            // update
            if (updateContext == null)
                return entity;
            switch (updateContext.getUpdateKind()){
                case FULL:
                    relationalEntityManager.update(updateContext.getDetachedOldEntity(), entity);
                    clearContext();
                    break;
                case PARTIAL:
//                    relationalEntityManager.partialUpdate(updateContext.getDetachedOldEntity(), entity, updateContext.getDetachedUpdateEntity());
//                    relationalEntityManager.partialUpdate(updateContext.getDetachedOldEntity(), ProxyUtils.hibernateUnproxyRaw(entity), updateContext.getDetachedUpdateEntity());
                    // todo infer membersToCheck cached again in RelationalServiceUpdateAdvice from single source and pass down this method
                    relationalEntityManager.partialUpdate(updateContext.getDetachedOldEntity(), ProxyUtils.hibernateUnproxyRaw(entity));
//                    relationalEntityManager.partialUpdate(updateContext.getDetachedOldEntity(), updateContext.getDetachedUpdateEntity());
                    clearContext();
                    break;
                case SOFT:
                    clearContext();
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

    protected void clearContext(){
        SubServiceCallContext subContext = ServiceCallContextHolder.getSubContext();
        if (subContext != null)
            subContext.clearValue(RELATIONAL_UPDATE_CONTEXT_KEY);
    }

//    @Before(value = "com.github.vincemann.springrapid.core.advice.SystemArchitecture.updateOperation() && " +
//            "com.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
//            "args(entity)")
//    public void preUpdateEntity(JoinPoint joinPoint, IdentifiableEntity entity) throws EntityNotFoundException, BadEntityException {
//        relationalEntityManager.update(entity,RelationalAdviceContext.isFullUpdate());
//        RelationalAdviceContext.clear();
//    }

    protected Optional<IdentifiableEntity> resolveById(JoinPoint joinPoint, Serializable id) {
        SimpleJpaRepository repo = AopTestUtils.getUltimateTargetObject(joinPoint.getTarget());
        Class entityClass = RepositoryUtil.getRepoType(repo);
        return entityLocator.findEntity(entityClass,id);
    }

    protected Optional<IdentifiableEntity> repoResolveById(JoinPoint joinPoint, Serializable id) {
        SimpleJpaRepository repo = AopTestUtils.getUltimateTargetObject(joinPoint.getTarget());
        Class entityClass = RepositoryUtil.getRepoType(repo);
        return ((AbstractCrudService) crudServiceLocator.find(entityClass)).getRepository().findById(id);
    }

    @Autowired
    public void setCrudServiceLocator(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }

    @Autowired
    public void setEntityLocator(EntityLocator entityLocator) {
        this.entityLocator = entityLocator;
    }

    @Autowired
    public void setRelationalEntityManager(RelationalEntityManager relationalEntityManager) {
        this.relationalEntityManager = relationalEntityManager;
    }
}
