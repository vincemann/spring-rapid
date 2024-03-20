package com.github.vincemann.springrapid.sync.softdelete;

import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public interface SoftDeleteAuditingRepository<E extends ISoftDeleteEntity<Id>,Id extends Serializable> {

    List<SoftDeleteEntityUpdateInfo> findUpdateInfosSince(Timestamp until, Specification<E> specification);
    List<E> findEntitiesUpdatedSince(Timestamp until, Specification<E> specification);
    SoftDeleteEntityUpdateInfo findUpdateInfo(Id id);
}
