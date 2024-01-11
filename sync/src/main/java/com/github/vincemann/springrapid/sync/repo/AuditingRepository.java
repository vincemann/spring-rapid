package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import org.springframework.data.jpa.repository.Query;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface AuditingRepository<E extends AuditingEntity<Id>,Id extends Serializable>
{
//    @Query("SELECT e FROM E e ORDER BY e.lastModifiedDate DESC")
    List<E> findAllSortedByLastModifiedDate();
}
