package com.github.vincemann.springrapid.sync;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables auditing collections on service level ( either class or method level of {@link com.github.vincemann.springrapid.core.service.CrudService} )
 * Implemented by {@link AuditCollectionAdvice}
 * @see AuditCollection
 * @see AuditCollectionAdvice
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface EnableAuditCollection {
}
