package com.github.vincemann.springrapid.syncdemo.controller.sync;

import com.github.vincemann.springrapid.sync.controller.SyncEntityController;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.service.sync.OwnerSyncService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.List;

@Controller
public class OwnerSyncController extends SyncEntityController<Owner,Long, OwnerSyncService> {

    @RequestMapping("/api/core/owner/sync-statuses-with-telprefix")
    public List<EntitySyncStatus> fetchSyncStatusesSinceTsWithTelnrPrefix(@RequestParam("ts") long timestamp, @RequestParam("prefix") String prefix){
        return getService().findEntitySyncStatusesSinceTimestampWithTelnrPrefix(
                new Timestamp(timestamp), prefix);
    }
}
