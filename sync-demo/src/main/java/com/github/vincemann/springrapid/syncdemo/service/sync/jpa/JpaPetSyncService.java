package com.github.vincemann.springrapid.syncdemo.service.sync.jpa;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.sync.service.JpaSyncService;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.repo.PetRepository;
import com.github.vincemann.springrapid.syncdemo.service.sync.PetSyncService;

@ServiceComponent
public class JpaPetSyncService extends JpaSyncService<Pet,Long> implements PetSyncService {
}
