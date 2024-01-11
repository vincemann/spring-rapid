package com.github.vincemann.springrapid.sync.repo;

import java.io.Serializable;
import java.util.Date;

public interface CustomAuditingRepository<Id extends Serializable> {
    Date findLastModifiedDateByIdEquals(Id id);
}
