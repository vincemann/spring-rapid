package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Needs to be combined with {@link EnableAuditCollection}.
 * Annotate collection fields of entity x, that is normally not detected by springs auditing. When changed, triggers the update of {@link AuditingEntity#getLastModifiedDate()}
 * and {@link AuditingEntity#getLastModifiedById()} .
 * Usually used to detect changes to foreign-key relations with other entities.
 *
 * Only works for direct updates via {@link com.github.vincemann.springrapid.core.service.CrudService#partialUpdate(IdentifiableEntity, String...)}
 * example:
 * class FooEntity{
 *     @AuditCollection
 *     private Set<BarEntity> subEntities;
 * }
 *
 *
 * fooService.partialUpdate(fooEntity,"subEntities") -> works
 *
 *
 * barService.partialUpdate(barEntity) -> does not update timestamp of fooEntity even with annotation present, bc only indirect update
 *
 * Is implemented via aop {@link com.github.vincemann.springrapid.sync.advice.AuditCollectionAdvice}.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface AuditCollection {
}
