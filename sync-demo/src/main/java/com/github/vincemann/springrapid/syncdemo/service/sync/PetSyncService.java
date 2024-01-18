package com.github.vincemann.springrapid.syncdemo.service.sync;

import com.github.vincemann.springrapid.sync.service.SyncService;
import com.github.vincemann.springrapid.syncdemo.model.Pet;

public interface PetSyncService extends SyncService<Pet,Long> {
}
