package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.core.model.audit.IAuditingEntity;
import com.github.vincemann.springrapid.sync.model.EntityUpdateInfo;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public interface AuditingRepository<E extends IAuditingEntity<Id>,Id extends Serializable>
{
//    @Query("SELECT e FROM E e ORDER BY e.lastModifiedDate DESC")
    List<EntityUpdateInfo> findUpdateInfosSince(Timestamp until, Specification<E> filters);
    List<E> findEntitiesUpdatedSince(Timestamp until, Specification<E> filters);
    EntityUpdateInfo findUpdateInfo(Id id);
}
