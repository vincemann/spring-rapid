package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.sync.model.EntityLastUpdateInfo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public interface CustomAuditingRepository<E extends AuditingEntity<Id>,Id extends Serializable>
{
//    @Query("SELECT e FROM E e ORDER BY e.lastModifiedDate DESC")
    List<EntityLastUpdateInfo> findLastUpdateInfosSince(Timestamp until, List<QueryFilter<? super E>> filters);
    List<E> findEntitiesLastUpdatedSince(Timestamp until, List<QueryFilter<? super E>> filters);
    Date findLastModifiedDateById(Id id);
}
