package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.CrudServiceLocator;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.Entity;
import com.github.vincemann.springrapid.core.util.NullAwareBeanUtils;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import com.github.vincemann.springrapid.core.util.ReflectionUtils;
import com.github.vincemann.springrapid.sync.model.AuditId;
import com.github.vincemann.springrapid.sync.model.AuditLog;
import com.github.vincemann.springrapid.sync.model.EntityDtoMapping;
import com.github.vincemann.springrapid.sync.repo.AuditLogRepository;
import com.github.vincemann.springrapid.sync.repo.EntityDtoMappingRepository;
import com.github.vincemann.springrapid.sync.service.AuditLogService;
import com.github.vincemann.springrapid.sync.util.ReflectionPropertyMatcher;
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
import java.util.stream.Collectors;

@Aspect
// should get executed within transaction of service, so when anything fails, the timestamp update is rolled back
@Order(Ordered.LOWEST_PRECEDENCE-1)
public class AuditAdvice {


    private AuditLogService auditLogService;

    @Before(
            value = "com.github.vincemann.springrapid.core.RapidArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.fullUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreJdkProxies() && " +
                    "args(update)")
    public void beforeFullUpdate(JoinPoint joinPoint, IdentifiableEntity update) throws EntityNotFoundException, BadEntityException {
        if (skip(joinPoint))
            return;

        assertTransactionActive();

        auditLogService.updateAuditLog(update);
    }

    @Before(
            value = "com.github.vincemann.springrapid.core.RapidArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.partialUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreJdkProxies() && " +
                    "args(update,fieldsToUpdate)")
    public void beforePartialUpdate(JoinPoint joinPoint, IdentifiableEntity update, String... fieldsToUpdate) {
        if (skip(joinPoint))
            return;

        assertTransactionActive();

        auditLogService.updateAuditLog(update, Entity.findPartialUpdatedFields(update,fieldsToUpdate));
    }



    @Before(
            value = "com.github.vincemann.springrapid.core.RapidArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.saveOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreJdkProxies() && " +
                    "args(entity)")
    public void beforeCreate(JoinPoint joinPoint, IdentifiableEntity entity) {
        if (skip(joinPoint))
            return;

        assertTransactionActive();

        auditLogService.updateAuditLog(entity);
    }

    protected boolean skip(JoinPoint joinPoint) {
        // the ignore pointcuts sometimes dont work as expected
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return true;
//        if (AutoBiDirUtils.isDisabled(joinPoint)) {
//            return true;
//        }
        return false;
    }

    protected void assertTransactionActive() {
        boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        if (!actualTransactionActive)
            throw new IllegalArgumentException("service method must be called within transaction, otherwise auto bidir wont work. User @DisableAutoBiDir to disable auto bidir management for this method, if you want to ignore");
    }

    @Autowired
    public void setAuditLogService(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }
}
