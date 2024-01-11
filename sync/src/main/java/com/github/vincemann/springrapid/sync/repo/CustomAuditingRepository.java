package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.core.model.AuditingEntity;

import java.io.Serializable;
import java.util.List;

public interface CustomAuditingRepository<E extends AuditingEntity<?>>
{
//    @Query("SELECT e FROM E e ORDER BY e.lastModifiedDate DESC")
    List<E> findAllSortedByLastModifiedDate();
}
