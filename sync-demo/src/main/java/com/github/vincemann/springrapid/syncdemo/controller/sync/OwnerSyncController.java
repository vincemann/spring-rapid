package com.github.vincemann.springrapid.syncdemo.controller.sync;

import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.sync.controller.SyncEntityController;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.service.sync.JpaOwnerSyncService;

import java.util.HashSet;

@WebController
public class OwnerSyncController
        extends SyncEntityController<Owner,Long, JpaOwnerSyncService> {

}
