package com.github.vincemann.springrapid.syncdemo.service.jpa.sync;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.sync.service.JpaSyncService;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.repo.OwnerRepository;

@ServiceComponent
public class JpaOwnerSyncService extends JpaSyncService<Owner,Long> {
}
