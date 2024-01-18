package com.github.vincemann.springrapid.syncdemo.service.sync.jpa;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.sync.service.JpaSyncService;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.syncdemo.service.sync.OwnerSyncService;

@ServiceComponent
public class JpaOwnerSyncService extends JpaSyncService<Owner,Long> implements OwnerSyncService {
}
