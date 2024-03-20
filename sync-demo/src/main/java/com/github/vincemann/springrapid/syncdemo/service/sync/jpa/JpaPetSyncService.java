package com.github.vincemann.springrapid.syncdemo.service.sync.jpa;

import com.github.vincemann.springrapid.sync.service.DefaultSyncService;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.service.sync.PetSyncService;
import org.springframework.stereotype.Service;

@Service
public class JpaPetSyncService
        extends DefaultSyncService<Pet,Long>
                implements PetSyncService {
}
