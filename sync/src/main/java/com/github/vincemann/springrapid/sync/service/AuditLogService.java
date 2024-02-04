package com.github.vincemann.springrapid.sync.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.sync.model.AuditId;
import com.github.vincemann.springrapid.sync.model.AuditLog;

import java.util.Set;

public interface AuditLogService {
    void updateAuditLog(AuditId id, Set<String> properties);
    /**
     * updates all mappings of auditlog to now
     */
    void updateAuditLog(AuditId id);


    AuditLog findOrCreateAuditLog(AuditId id);
}
