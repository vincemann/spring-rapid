package com.github.vincemann.springrapid.acldemo.service.user.del;

import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import org.springframework.beans.factory.annotation.Autowired;

public class DelegatingUserService
        extends AbstractDelegatingUserService
{

    @Autowired
    @Override
    public void setOwnerService(OwnerService ownerService) {
        super.setOwnerService(ownerService);
    }

    @Autowired
    @Override
    public void setVetService(VetService vetService) {
        super.setVetService(vetService);
    }
}
