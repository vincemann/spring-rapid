package com.github.vincemann.springrapid.core.model.audit;

import java.io.Serializable;

public interface AuditTemplate {


    public void updateLastModified(AuditingEntity<?> entity);

    void updateLastModified(AuditingEntity entity, Serializable auditorId);
}
