package com.github.vincemann.springrapid.sync.repo;

import com.github.vincemann.springrapid.sync.model.AuditId;
import com.github.vincemann.springrapid.sync.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, AuditId> {
}
