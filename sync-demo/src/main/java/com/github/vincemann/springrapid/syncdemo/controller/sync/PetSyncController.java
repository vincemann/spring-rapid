package com.github.vincemann.springrapid.syncdemo.controller.sync;

import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.sync.controller.SyncEntityController;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.service.filter.PetParentFilter;
import com.github.vincemann.springrapid.syncdemo.service.sync.PetSyncService;
import org.springframework.beans.factory.annotation.Autowired;

@WebController
public class PetSyncController extends SyncEntityController<Pet,Long, PetSyncService> {

    @Autowired
    public void configureAllowedExtensions(PetParentFilter petsOfOwnerFilter) {
        registerExtensions(petsOfOwnerFilter);
    }
}
