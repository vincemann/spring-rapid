package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.CrudServiceLocator;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.NullAwareBeanUtils;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import com.github.vincemann.springrapid.core.util.ReflectionUtils;
import com.github.vincemann.springrapid.sync.model.AuditId;
import com.github.vincemann.springrapid.sync.model.AuditLog;
import com.github.vincemann.springrapid.sync.model.EntityDtoMapping;
import com.github.vincemann.springrapid.sync.repo.AuditLogRepository;
import com.github.vincemann.springrapid.sync.repo.EntityDtoMappingRepository;
import com.google.common.collect.Sets;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.test.util.AopTestUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Aspect
// should get executed within transaction of service, so when anything fails, the timestamp update is rolled back
@Order(Ordered.LOWEST_PRECEDENCE-1)
public class AuditAdvice {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private EntityDtoMappingRepository entityDtoMappingRepository;


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

        assertTransactionActive();

        updateAuditLog(update);



    }

    protected void updateAuditLog(IdentifiableEntity entity, Set<String> properties){
        AuditId id = getId(entity);
        Optional<AuditLog> auditLog = auditLogRepository.findById(id);
        if (auditLog.isEmpty()){
            throw new IllegalArgumentException("no audit log found for entity: " + entity);
        }
        Set<EntityDtoMapping> matchingMappings = findMatchingMappings(auditLog,properties);
        updateMappingsTimestamp(matchingMappings);
    }

    /**
     * updates all mappings of auditlog to now
     */
    protected void updateAuditLog(IdentifiableEntity entity){
        AuditId id = getId(entity);
        Optional<AuditLog> auditLog = auditLogRepository.findById(id);
        if (auditLog.isEmpty()){
            throw new IllegalArgumentException("no audit log found for entity: " + entity);
        }
        updateMappingsTimestamp(auditLog.get().getDtoMappings());
    }

    protected void updateMappingsTimestamp(Set<EntityDtoMapping> mappings){
        mappings.forEach(mapping -> mapping.setLastUpdateTime(LocalDateTime.now()));
    }

    protected AuditId getId(IdentifiableEntity entity){
        return new AuditId(entity.getClass().getName(),entity.getId().toString());
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

        assertTransactionActive();

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

        assertTransactionActive();

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

    protected void assertTransactionActive(){
        boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        if (!actualTransactionActive)
            throw new IllegalArgumentException("service method must be called within transaction, otherwise auto bidir wont work. User @DisableAutoBiDir to disable auto bidir management for this method, if you want to ignore");
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
