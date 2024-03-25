package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.sync.model.entity.IAuditingEntity;
import com.github.vincemann.springrapid.sync.model.EntityUpdateInfo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Entity repository providing methods for finding {@link EntityUpdateInfo} for specific entity or entities.
 * @param <E> entity type, must implement {@link IAuditingEntity}
 * @param <Id> id type of entity
 *
 * @see com.github.vincemann.springrapid.sync.service.SyncService
 */
public interface AuditingRepository<E extends IAuditingEntity<Id>,Id extends Serializable>
{
    List<EntityUpdateInfo> findUpdateInfosSince(Timestamp until, @Nullable Specification<E> spec);
    List<E> findEntitiesUpdatedSince(Timestamp until, @Nullable Specification<E> spec);
    EntityUpdateInfo findUpdateInfo(Id id);
}
