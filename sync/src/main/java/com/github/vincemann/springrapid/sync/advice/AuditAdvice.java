package com.github.vincemann.springrapid.sync.advice;

import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;
import com.github.vincemann.springrapid.core.util.Entity;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import com.github.vincemann.springrapid.sync.AuditField;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Set;

import static com.github.vincemann.springrapid.core.util.ReflectionUtils.findFieldsAnnotatedWith;

/**
 * Implements logic related to {@link AuditField}.
 */
@Aspect
// should get executed within transaction of service, so when anything fails, the timestamp update is rolled back
@Order(Ordered.LOWEST_PRECEDENCE-1)
@Slf4j
public class AuditAdvice {



    @AfterReturning(
            value = "com.github.vincemann.springrapid.core.RapidArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.partialUpdateOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreExtensions() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.ignoreJdkProxies() && " +
                    "args(update,fieldsToUpdate)", returning = "result")
    public void afterPartialUpdate(JoinPoint joinPoint,AuditingEntity result, AuditingEntity update, String... fieldsToUpdate) {
        if (skip(joinPoint))
            return;

        Set<Field> auditedCollectionFields = findFieldsAnnotatedWith(update.getClass(), AuditField.class);
        if (auditedCollectionFields.isEmpty())
            return;

        assertTransactionActive();

        Set<String> updatedFields = Entity.findPartialUpdatedFields(update, fieldsToUpdate);

        for (Field field : auditedCollectionFields) {
            if (updatedFields.contains(field.getName())){
                // updates detected, should also trigger dirty checking so AuditingEntityHandler also sets lastModifiedById
                // https://stackoverflow.com/a/63777063/9027032
                result.setLastModifiedDate(new Date());
            }
        }
    }



    protected boolean skip(JoinPoint joinPoint) {
        // the ignore pointcuts sometimes dont work as expected
        if (!ProxyUtils.isRootService(joinPoint.getTarget()))
            return true;
        return false;
    }


    protected void assertTransactionActive() {
        boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        if (!actualTransactionActive)
            throw new IllegalArgumentException("service method must be called within transaction, otherwise auto bidir wont work. User @DisableAutoBiDir to disable auto bidir management for this method, if you want to ignore");
    }

}
