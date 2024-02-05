package com.github.vincemann.springrapid.sync.advice;

import com.github.vincemann.springrapid.core.model.audit.AuditTemplate;
import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;
import com.github.vincemann.springrapid.core.util.Entity;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import com.github.vincemann.springrapid.sync.AuditCollection;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Field;
import java.util.Set;

import static com.github.vincemann.springrapid.core.util.ReflectionUtils.findCollectionFieldsAnnotatedWith;

/**
 * Implements logic related to {@link AuditCollection}.
 */
@Aspect
// should get executed within transaction of service, so when anything fails, the timestamp update is rolled back
@Order(Ordered.LOWEST_PRECEDENCE-1)
@Slf4j
public class AuditCollectionsAdvice {


    private AuditTemplate auditTemplate;


    @Before(
            value = "com.github.vincemann.springrapid.core.RapidArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.partialUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreJdkProxies() && " +
                    "args(update,fieldsToUpdate)")
    public void beforePartialUpdate(JoinPoint joinPoint, AuditingEntity update, String... fieldsToUpdate) {
        if (skip(joinPoint))
            return;

        Set<Field> auditedCollectionFields = findCollectionFieldsAnnotatedWith(update.getClass(), AuditCollection.class);
        if (auditedCollectionFields.isEmpty())
            return;

        assertTransactionActive();

        Set<String> updatedFields = Entity.findPartialUpdatedFields(update, fieldsToUpdate);

        for (Field field : auditedCollectionFields) {
            if (updatedFields.contains(field.getName()))
                auditTemplate.updateLastModified(update);
        }
    }



    protected boolean skip(JoinPoint joinPoint) {
        // the ignore pointcuts sometimes dont work as expected
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return true;
        return false;
    }

    @Autowired
    public void setAuditTemplate(AuditTemplate auditTemplate) {
        this.auditTemplate = auditTemplate;
    }

    protected void assertTransactionActive() {
        boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        if (!actualTransactionActive)
            throw new IllegalArgumentException("service method must be called within transaction, otherwise auto bidir wont work. User @DisableAutoBiDir to disable auto bidir management for this method, if you want to ignore");
    }

}
