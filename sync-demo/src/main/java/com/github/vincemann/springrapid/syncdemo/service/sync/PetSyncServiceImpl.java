package com.github.vincemann.springrapid.syncdemo.service.sync;

import com.github.vincemann.springrapid.sync.service.DefaultSyncService;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import org.springframework.stereotype.Service;

@Service
public class PetSyncServiceImpl
        extends DefaultSyncService<Pet,Long>
                implements PetSyncService {
}
