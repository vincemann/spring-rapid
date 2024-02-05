package com.github.vincemann.springrapid.core.model.audit;

import java.io.Serializable;

public interface AuditTemplate {


    public void updateLastModified(IAuditingEntity entity);

    void updateLastModified(IAuditingEntity entity, Serializable auditorId);
}
