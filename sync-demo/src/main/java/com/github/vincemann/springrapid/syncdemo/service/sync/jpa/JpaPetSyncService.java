package com.github.vincemann.springrapid.syncdemo.service.sync.jpa;

import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.sync.service.JpaSyncService;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.repo.PetRepository;
import com.github.vincemann.springrapid.syncdemo.service.sync.PetSyncService;

@Component
public class JpaPetSyncService extends JpaSyncService<Pet,Long> implements PetSyncService {
}
