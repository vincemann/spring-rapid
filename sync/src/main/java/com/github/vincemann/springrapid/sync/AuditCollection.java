package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.core.model.AuditingEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


//
/**
 * annotate collection fields of entities for which, when changed, the {@link AuditingEntity#getLastModifiedDate()}
 * and {@link AuditingEntity#getLastModifiedById()} should be updated.
 */
@Retention(RetentionPolicy.RUNTIME) // This determines when the annotation is accessible.
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface AuditCollection {
}
