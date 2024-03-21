package com.github.vincemann.springrapid.syncdemo.controller.sync;

import com.github.vincemann.springrapid.sync.controller.SyncEntityController;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.service.filter.OwnerTelNumberFilter;
import com.github.vincemann.springrapid.syncdemo.service.sync.OwnerSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class OwnerSyncController extends SyncEntityController<Owner,Long, OwnerSyncService> {

    @Autowired
    public void registerAllowedExtensions(OwnerTelNumberFilter ownerTelNumberFilter) {
        registerExtensions(ownerTelNumberFilter);
    }
}
