package com.github.vincemann.springrapid.syncdemo.controller.sync;

import com.github.vincemann.springrapid.syncdemo.dto.pet.PetDto;
import org.springframework.stereotype.Controller;
import com.github.vincemann.springrapid.sync.controller.SyncEntityController;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.service.filter.PetParentFilter;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class PetSyncController extends SyncEntityController<Pet,Long> {

    @Autowired
    public void registerAllowedExtensions(PetParentFilter petsOfOwnerFilter) {
        registerExtensions(petsOfOwnerFilter);
    }

}
