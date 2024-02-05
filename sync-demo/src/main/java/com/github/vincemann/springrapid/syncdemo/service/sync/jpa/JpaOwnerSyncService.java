package com.github.vincemann.springrapid.syncdemo.service.sync.jpa;

import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.sync.service.JpaSyncService;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.syncdemo.service.sync.OwnerSyncService;
import org.springframework.stereotype.Service;

@Service
public class JpaOwnerSyncService
        extends JpaSyncService<Owner,Long>
                implements OwnerSyncService {
}
