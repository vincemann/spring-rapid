package com.github.vincemann.springrapid.sync.service;


import com.github.vincemann.springrapid.core.model.audit.IAuditingEntity;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.LastFetchInfo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SyncService<E extends IAuditingEntity<Id>,Id extends Serializable> {


    EntitySyncStatus findEntitySyncStatus(LastFetchInfo clientLastUpdate) throws EntityNotFoundException;

    Set<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp clientLastUpdate, List<QueryFilter<? super E>> jpqlFilters);

    Set<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp clientLastUpdate, List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> entityFilters);

    Set<EntitySyncStatus> findEntitySyncStatuses(Collection<LastFetchInfo> clientLastUpdates) throws EntityNotFoundException;
}
