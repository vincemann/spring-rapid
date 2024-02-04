package com.github.vincemann.springrapid.syncdemo.controller.sync;

import com.github.vincemann.springrapid.sync.DtoClassRegistry;
import com.github.vincemann.springrapid.sync.controller.SyncEntityController;
import com.github.vincemann.springrapid.syncdemo.dto.owner.own.ReadOwnDetailedOwnerDto;
import com.github.vincemann.springrapid.syncdemo.dto.owner.own.ReadOwnOverviewOwnerDto;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.service.filter.OwnerTelNumberFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class OwnerSyncController extends SyncEntityController<Owner,Long> {

    @Autowired
    public void registerAllowedExtensions(OwnerTelNumberFilter ownerTelNumberFilter) {
        registerExtensions(ownerTelNumberFilter);
    }

    @Override
    protected void configureDtoClassRegistry(DtoClassRegistry registry) {
        registry.register("detail", ReadOwnDetailedOwnerDto.class);
        registry.register("overview", ReadOwnOverviewOwnerDto.class);
    }
}
