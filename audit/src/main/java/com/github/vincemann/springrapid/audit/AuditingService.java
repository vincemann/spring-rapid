package com.github.vincemann.springrapid.audit;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;

public interface AuditingService<Id extends Serializable> {
    @Transactional
    Date findLastModifiedDate(Id id);
}
