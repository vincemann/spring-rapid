package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.sync.model.AuditLog;

public interface AuditLogFactory {

    public AuditLog create(IdentifiableEntity entity);
}
