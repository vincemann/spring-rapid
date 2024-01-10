package com.github.vincemann.springrapid.sync;

import java.io.Serializable;
import java.util.Date;

public interface AuditingRepository<Id extends Serializable>
{
    Date findLastModifiedDateByIdEquals(Id id);
}
