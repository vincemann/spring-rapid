package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.service.JPQLEntityFilter;
import com.github.vincemann.springrapid.sync.model.EntityLastUpdateInfo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface CustomAuditingRepository<E extends AuditingEntity<?>>
{
//    @Query("SELECT e FROM E e ORDER BY e.lastModifiedDate DESC")
    List<EntityLastUpdateInfo> findLastUpdateInfosSince(Timestamp until, List<JPQLEntityFilter<E>> filters);
}
