package com.github.vincemann.springrapid.syncdemo.service.sync.jpa;

import com.github.vincemann.springrapid.sync.service.DefaultSyncService;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.service.sync.OwnerSyncService;
import org.springframework.stereotype.Service;

@Service
public class JpaOwnerSyncService
        extends DefaultSyncService<Owner,Long>
                implements OwnerSyncService
{
}
