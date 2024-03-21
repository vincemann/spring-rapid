package com.github.vincemann.springrapid.syncdemo.controller.sync;

import com.github.vincemann.springrapid.sync.controller.SyncEntityController;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.service.sync.PetSyncService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.List;

@Controller
public class PetSyncController extends SyncEntityController<Pet,Long, PetSyncService> {

    @GetMapping("/api/core/pet/sync-statuses-since-ts-of-owner")
    public List<EntitySyncStatus> fetchSyncStatusesSinceTsOfOwner(@RequestParam("ts") long timestamp, @RequestParam("owner") long ownerId){
        return getService().findEntitySyncStatusesSinceTimestampOfOwner(
                new Timestamp(timestamp), ownerId);
    }

}
