package com.github.vincemann.springrapid.sync;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.sync.dto.EntityLastUpdateInfo;
import com.github.vincemann.springrapid.sync.dto.EntitySyncStatus;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Set;

public interface AuditingService<Id extends Serializable> {

    @Transactional
    EntitySyncStatus findEntitySyncStatus(EntityLastUpdateInfo lastUpdateInfo) throws EntityNotFoundException;

    @Transactional
    Set<EntitySyncStatus> findEntitiesSyncStatus(Set<EntityLastUpdateInfo> lastUpdateInfos) throws EntityNotFoundException;
}
