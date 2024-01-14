package com.github.vincemann.springrapid.sync.service;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.sync.model.EntityLastUpdateInfo;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SyncService<E extends AuditingEntity<Id>,Id extends Serializable> {


    EntitySyncStatus findEntitySyncStatus(EntityLastUpdateInfo lastUpdateInfo);

    Set<EntitySyncStatus> findEntitySyncStatusesSinceTimestamp(Timestamp lastUpdate, List<QueryFilter<E>> jpqlFilters);


    Set<EntitySyncStatus> findEntitySyncStatuses(Collection<EntityLastUpdateInfo> lastUpdateInfos);
}
