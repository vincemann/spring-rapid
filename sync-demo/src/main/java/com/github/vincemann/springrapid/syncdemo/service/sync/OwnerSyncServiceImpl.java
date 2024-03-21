package com.github.vincemann.springrapid.syncdemo.service.sync;

import com.github.vincemann.springrapid.sync.service.DefaultSyncService;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import org.springframework.stereotype.Service;

@Service
public class OwnerSyncServiceImpl
        extends DefaultSyncService<Owner,Long>
                implements OwnerSyncService
{
}
