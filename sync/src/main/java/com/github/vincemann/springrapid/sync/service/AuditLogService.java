package com.github.vincemann.springrapid.sync.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

import java.util.Set;

public interface AuditLogService {
    void updateAuditLog(IdentifiableEntity entity, Set<String> properties);
    /**
     * updates all mappings of auditlog to now
     */
    public void updateAuditLog(IdentifiableEntity entity);
}
