package com.github.vincemann.springrapid.autobidir.advice;

import com.github.vincemann.springrapid.autobidir.AutoBiDirUtils;
import com.github.vincemann.springrapid.autobidir.RelationalAdviceContext;
import com.github.vincemann.springrapid.autobidir.RelationalEntityManager;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.service.context.SubServiceCallContext;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.util.Optional;

import static com.github.vincemann.springrapid.autobidir.advice.RelationalServiceUpdateAdvice.RELATIONAL_UPDATE_CONTEXT_KEY;

@Aspect
@Slf4j
@Order(3)
public class RelationalEntityAdvice {

//    private EntityLocator entityLocator;
    private RelationalEntityManager relationalEntityManager;

    private CrudServiceLocator crudServiceLocator;


    @Before("com.github.vincemann.springrapid.core.SystemArchitecture.deleteOperation() && " +
            "com.github.vincemann.springrapid.core.SystemArchitecture.repoOperation() && " +
            "args(id)")
    public void preRemoveEntity(JoinPoint joinPoint, Serializable id) throws Throwable {

//        System.err.println("PRE REMOVE: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (AutoBiDirUtils.isDisabled(joinPoint)){
            return;
        }

        Optional<IdentifiableEntity> entity = repoResolveById(joinPoint,id);
        if (entity.isPresent()) {
            relationalEntityManager.remove(entity.get());
        } else {
            log.warn("preDelete BiDirEntity could not be done, because for id: " + id + " was no entity found");
        }
    }


    @Before("com.github.vincemann.springrapid.core.SystemArchitecture.saveOperation() && " +
            "com.github.vincemann.springrapid.core.SystemArchitecture.repoOperation() && " +
            "args(entity)")
    public void prePersistEntity(JoinPoint joinPoint, IdentifiableEntity entity) throws Throwable {

//        System.err.println("PRE PERSIST: " + joinPoint.getTarget() + "->" + joinPoint.getSignature().getName());

        if (AutoBiDirUtils.isDisabled(joinPoint)){
            return;
        }


        RelationalAdviceContext updateContext = null;
        if (ServiceCallContextHolder.getSubContext() != null)
            updateContext = ServiceCallContextHolder.getSubContext().getValue(RELATIONAL_UPDATE_CONTEXT_KEY);


        if (updateContext == null){
            // update context not set on direct repo calls or crud service updated calls, when service was not properly wrapped with aop proxy
            if (entity.getId() == null){
                // save operation and update context null
                relationalEntityManager.save(entity);
                clearSubContext();
                return;
            }else{
                if (log.isWarnEnabled()){
                    log.warn("Update context is null - only limited auto-rel management possible");
                    log.warn("If this is no direct call on repo, make sure your crud service bean is wrapped with aop proxy");
                    log.warn("Update context null and update operation with set id on repo - assuming full update");
                }

//                // repo is probably called directly, so also use repo to find old entity
                Optional<IdentifiableEntity> old = repoResolveById(joinPoint, entity.getId());
                VerifyEntity.isPresent(old,entity.getId(),entity.getClass());
                // full detach only works like that
                IdentifiableEntity detachedOld = MyJpaUtils.deepDetachOrGet(old.get());
                IdentifiableEntity detachedUpdateEntity = MyJpaUtils.deepDetachOrGet(entity);
                relationalEntityManager.update(entity,detachedOld, detachedUpdateEntity);
                clearSubContext();
                return;
            }
        }

        // update context is not null

        if (entity.getId() == null || updateContext.getOperationType()==null) {
            // save
            relationalEntityManager.save(entity);
            clearSubContext();
        } else {
            // update
            switch (updateContext.getOperationType()){
                case FULL:
                    relationalEntityManager.update(entity, updateContext.getDetachedOldEntity(),updateContext.getDetachedUpdateEntity());
                    clearSubContext();
                    break;
                case PARTIAL:
                    String[] whiteListedFieldsToUpdate = updateContext.getWhiteListedFields().toArray(new String[0]);
//                    System.err.println(Arrays.toString(whiteListedFieldsToUpdate));
                    relationalEntityManager.partialUpdate(entity, updateContext.getDetachedOldEntity(),
                            updateContext.getDetachedUpdateEntity(), whiteListedFieldsToUpdate);
                    clearSubContext();
                    break;
                case SOFT:
                    clearSubContext();
                    break;
            }
        }
    }

    protected void clearSubContext(){
        SubServiceCallContext subContext = ServiceCallContextHolder.getSubContext();
        if (subContext != null)
            subContext.clearValue(RELATIONAL_UPDATE_CONTEXT_KEY);
    }

//    protected Optional<IdentifiableEntity> resolveById(JoinPoint joinPoint, Serializable id) {
//        SimpleJpaRepository repo = AopTestUtils.getUltimateTargetObject(joinPoint.getTarget());
//        Class entityClass = RepositoryUtil.getRepoType(repo);
//        return entityLocator.findEntity(entityClass,id);
//    }

    // todo just create repoLocator
    protected Optional<IdentifiableEntity> repoResolveById(JoinPoint joinPoint, Serializable id) {
        SimpleJpaRepository repo = AopTestUtils.getUltimateTargetObject(joinPoint.getTarget());
        Class entityClass = RepositoryUtil.getRepoType(repo);
        CrudService service = crudServiceLocator.find(entityClass);
        if (ProxyUtils.isJDKProxy(service)){
            return ((AbstractCrudService) ProxyUtils.getExtensionProxy(service).getLast()).getRepository().findById(id);
        }else{
            return (((AbstractCrudService) service).getRepository().findById(id));
        }
    }

    @Autowired
    public void setCrudServiceLocator(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }

//    @Autowired
//    public void setEntityLocator(EntityLocator entityLocator) {
//        this.entityLocator = entityLocator;
//    }

    @Autowired
    public void setRelationalEntityManager(RelationalEntityManager relationalEntityManager) {
        this.relationalEntityManager = relationalEntityManager;
    }
}
