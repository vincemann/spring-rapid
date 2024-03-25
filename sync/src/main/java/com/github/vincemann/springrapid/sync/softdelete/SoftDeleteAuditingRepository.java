package com.github.vincemann.springrapid.sync.softdelete;

import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Much like {@link com.github.vincemann.springrapid.sync.repo.AuditingRepository} but can also find out about soft delete timestamp.
 * @see SoftDeleteEntityUpdateInfo
 * @param <E> entity type, must implement {@link SoftDeleteEntity}
 * @param <Id> id type of entity
 */
public interface SoftDeleteAuditingRepository<E extends ISoftDeleteEntity<Id>,Id extends Serializable> {

    List<SoftDeleteEntityUpdateInfo> findUpdateInfosSince(Timestamp until, Specification<E> specification);
    List<E> findEntitiesUpdatedSince(Timestamp until, Specification<E> specification);
    SoftDeleteEntityUpdateInfo findUpdateInfo(Id id);
}
