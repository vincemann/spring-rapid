package com.github.vincemann.springrapid.syncdemo.controller.sync;

import com.github.vincemann.springrapid.sync.controller.SyncEntityController;
import com.github.vincemann.springrapid.sync.model.EntitySyncStatus;
import com.github.vincemann.springrapid.sync.model.SyncStatus;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.service.sync.OwnerSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.List;

@Controller
public class OwnerSyncController extends SyncEntityController<Owner,Long, OwnerSyncService> {

    @RequestMapping("/api/core/owner/sync-statuses-with-telprefix")
    public ResponseEntity<List<EntitySyncStatus>> fetchSyncStatusesSinceTsWithTelnrPrefix(@RequestParam("ts") long timestamp, @RequestParam("prefix") String prefix){
        List<EntitySyncStatus> statuses = getService().findEntitySyncStatusesSinceTimestampWithTelnrPrefix(
                new Timestamp(timestamp), prefix);
        return ResponseEntity.ok(statuses);
    }
}
