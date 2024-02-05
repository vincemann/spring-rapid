package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * annotate collection fields of entity x, that are normally not detected by springs auditing, that when changed, trigger the update of {@link AuditingEntity#getLastModifiedDate()}
 * and {@link AuditingEntity#getLastModifiedById()} .
 * This could be foreign relationships with entities or other collections
 *
 * Only works for direct updates via {@link com.github.vincemann.springrapid.core.service.CrudService#partialUpdate(IdentifiableEntity, String...)}
 * example:
 * class EntityX{
 *     @AuditCollection
 *     private Set<EntityY> subEntities;
 * }
 *
 *
 * entityXService.partialUpdate(entityX,"subEntities") -> works
 *
 * entityYService.partialUpdate(entityY) -> does not update timestamp of entityX even with annotation present, bc no direct update on EntityX
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface AuditCollection {
}
