package com.github.vincemann.springrapid.sync.advice;

import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;
import com.github.vincemann.springrapid.core.util.Entity;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import com.github.vincemann.springrapid.sync.AuditCollection;
import com.github.vincemann.springrapid.sync.EnableAuditCollection;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;

import static com.github.vincemann.springrapid.core.util.ReflectionUtils.findFieldsAnnotatedWith;

/**
 * Implements logic related to {@link AuditCollection}.
 */
@Aspect
// should get executed within transaction of service, so when anything fails, the timestamp update is rolled back
@Order(Ordered.LOWEST_PRECEDENCE-1)
@Slf4j
public class AuditCollectionAdvice {



    @AfterReturning(
            value = "com.github.vincemann.springrapid.core.RapidArchitecture.serviceOperation() && " +
                    "com.github.vincemann.springrapid.core.RapidArchitecture.partialUpdateOperation() && " +
                    "args(update,fieldsToUpdate)", returning = "result")
    public void afterPartialUpdate(JoinPoint joinPoint, AuditingEntity result, AuditingEntity update, String... fieldsToUpdate) {
        if (skip(joinPoint))
            return;

        Set<Field> auditedCollectionFields = findFieldsAnnotatedWith(update.getClass(), AuditCollection.class);
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



    /**
     * check if join point should be skipped
     * skip if current bean is not root service
     * check if enabled by looking for {@link com.github.vincemann.springrapid.sync.EnableAuditCollection}.
     */
    protected boolean skip(JoinPoint joinPoint) {
        return !isEnabled(joinPoint);
    }

    protected boolean isEnabled(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // check for class annotation first
        boolean enabledForClass = AnnotationUtils.findAnnotation(AopProxyUtils.ultimateTargetClass(joinPoint.getTarget()), EnableAuditCollection.class) != null;
        if (enabledForClass){
            // is enabled
            return true;
        }

        // maybe only enabled on method level
        if (method.isAnnotationPresent(EnableAuditCollection.class))
            return true;
        return false;
    }


    protected void assertTransactionActive() {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(),"service method must be called within transaction, otherwise auto bidir wont work. User @DisableAutoBiDir to disable auto bidir management for this method, if you want to ignore");
    }

}
