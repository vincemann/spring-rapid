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
import com.github.vincemann.springrapid.core.util.EntityLocator;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import com.github.vincemann.springrapid.core.util.RepositoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import static com.github.vincemann.springrapid.autobidir.advice.RelationalServiceUpdateAdvice.RELATIONAL_UPDATE_CONTEXT_KEY;

@Aspect
@Slf4j
@Order(3)
public class RelationalEntityAdvice {

    private EntityLocator entityLocator;
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
            if (log.isWarnEnabled())
                log.warn("update context is null - only limited auto-rel management possible");
            // repo is called directly, not from service via update- or save-methods
            if (entity.getId() == null){
                // save operation and update context null
                relationalEntityManager.save(entity);
                clearContext();
                return;
            }else{
                // todo just do full update and warning - fetch old entity from repo
                throw new IllegalArgumentException("Usage of repo directly is not permitted with set id yet, use Service api for updates");
//                // update context null and no safe operation
//                if (log.isWarnEnabled())
//                    log.warn("update context null and update operation - assuming full update");
////                throw new IllegalArgumentException("Cannot use update function yet without calling service upfront");
//
////                // repo is called directly, so also use repo to find old entity
//                Optional<IdentifiableEntity> old = repoResolveById(joinPoint, entity.getId());
//                VerifyEntity.isPresent(old,entity.getId(),entity.getClass());
//                // full detach only works like that
//                IdentifiableEntity oldEntity = BeanUtils.clone(ProxyUtils.hibernateUnproxyRaw(old.get()));
//                entityManager.detach(oldEntity);
//                relationalEntityManager.update(oldEntity, entity);
//                clearContext();
//                return entity;
            }
        }

        // update context is not null

        if (entity.getId() == null || updateContext.getOperationType()==null) {
            // save
            relationalEntityManager.save(entity);
            clearContext();
        } else {
            // update
            switch (updateContext.getOperationType()){
                case FULL:
                    relationalEntityManager.update(entity, updateContext.getDetachedOldEntity(),updateContext.getDetachedUpdateEntity());
                    clearContext();
                    break;
                case PARTIAL:
                    String[] whiteListedFieldsToUpdate = updateContext.getWhiteListedFields().toArray(new String[0]);
                    System.err.println(Arrays.toString(whiteListedFieldsToUpdate));
                    relationalEntityManager.partialUpdate(entity, updateContext.getDetachedOldEntity(),
                            updateContext.getDetachedUpdateEntity(), whiteListedFieldsToUpdate);
                    clearContext();
                    break;
                case SOFT:
                    clearContext();
                    break;
            }
        }
    }

    protected void clearContext(){
        SubServiceCallContext subContext = ServiceCallContextHolder.getSubContext();
        if (subContext != null)
            subContext.clearValue(RELATIONAL_UPDATE_CONTEXT_KEY);
    }

//    protected Optional<IdentifiableEntity> resolveById(JoinPoint joinPoint, Serializable id) {
//        SimpleJpaRepository repo = AopTestUtils.getUltimateTargetObject(joinPoint.getTarget());
//        Class entityClass = RepositoryUtil.getRepoType(repo);
//        return entityLocator.findEntity(entityClass,id);
//    }

    // todo just create repoLocator...
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

    @Autowired
    public void setEntityLocator(EntityLocator entityLocator) {
        this.entityLocator = entityLocator;
    }

    @Autowired
    public void setRelationalEntityManager(RelationalEntityManager relationalEntityManager) {
        this.relationalEntityManager = relationalEntityManager;
    }
}
