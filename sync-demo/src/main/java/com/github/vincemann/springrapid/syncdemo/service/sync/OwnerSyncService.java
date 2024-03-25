package com.github.vincemann.springrapid.syncdemo.service.sync;

import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.service.SyncService;
import com.github.vincemann.springrapid.syncdemo.model.Owner;

import java.sql.Timestamp;
import java.util.List;

public interface OwnerSyncService extends SyncService<Owner,Long> {
    List<EntitySyncStatus> findEntitySyncStatusesSinceTimestampWithTelnrPrefix(Timestamp lastClientUpdate, String prefix);

}
