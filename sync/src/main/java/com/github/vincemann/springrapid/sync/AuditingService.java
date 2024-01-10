package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.service.EntityFilter;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.sync.dto.EntityLastUpdateInfo;
import com.github.vincemann.springrapid.sync.dto.EntitySyncStatus;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

public interface AuditingService<E extends AuditingEntity<Id>,Id extends Serializable> {

    @Transactional
    EntitySyncStatus findEntitySyncStatus(EntityLastUpdateInfo lastUpdateInfo) throws EntityNotFoundException;

    @Transactional
    Set<EntitySyncStatus> findEntitySyncStatuses(Set<EntityLastUpdateInfo> lastUpdateInfos) throws EntityNotFoundException;

    @Transactional
    Set<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp lastUpdate, Set<EntityFilter<E>> filters) throws EntityNotFoundException;
}
