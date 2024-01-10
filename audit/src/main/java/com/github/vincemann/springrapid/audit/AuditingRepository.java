package com.github.vincemann.springrapid.audit;

import java.io.Serializable;
import java.util.Date;

public interface AuditingRepository<Id extends Serializable>
{

    Date findLastModifiedDateByIdEquals(Id id);

}
