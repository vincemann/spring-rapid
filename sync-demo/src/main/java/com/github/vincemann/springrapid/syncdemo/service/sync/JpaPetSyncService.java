package com.github.vincemann.springrapid.syncdemo.service.sync;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.sync.service.JpaSyncService;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.repo.PetRepository;

@ServiceComponent
public class JpaPetSyncService extends JpaSyncService<Pet,Long, PetRepository> {
}
