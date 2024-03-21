package com.github.vincemann.springrapid.syncdemo.service.sync;

import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.service.SyncService;
import com.github.vincemann.springrapid.syncdemo.model.Pet;

import java.sql.Timestamp;
import java.util.List;

public interface PetSyncService extends SyncService<Pet,Long> {
    List<EntitySyncStatus> findEntitySyncStatusesSinceTimestampOfOwner(Timestamp timestamp, long ownerId);
}
