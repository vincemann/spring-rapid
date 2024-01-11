package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.io.Serializable;
import java.util.Date;

public interface AuditingRepository<E extends AuditingEntity<Id>,Id extends Serializable>{
    Date findLastModifiedDateByIdEquals(Id id);
}
