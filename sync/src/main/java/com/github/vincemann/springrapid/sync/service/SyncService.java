package com.github.vincemann.springrapid.sync.service;


import com.github.vincemann.springrapid.sync.model.entity.IAuditingEntity;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.LastFetchInfo;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface SyncService<E extends IAuditingEntity<Id>,Id extends Serializable> {


    @Nullable
    EntitySyncStatus findEntitySyncStatus(LastFetchInfo clientLastUpdate) throws EntityNotFoundException;

    List<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp clientLastUpdate);

    List<EntitySyncStatus> findEntitySyncStatuses(Collection<LastFetchInfo> clientLastUpdates) throws EntityNotFoundException;
}
