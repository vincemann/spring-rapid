package com.github.vincemann.springrapid.acldemo.service.user.del;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import org.springframework.beans.factory.annotation.Autowired;

public class SecuredDelegatingUserService extends AbstractDelegatingUserService {



    @Autowired
    @Secured
    @Override
    public void setOwnerService(OwnerService ownerService) {
        super.setOwnerService(ownerService);
    }

    @Autowired
    @Secured
    @Override
    public void setVetService(VetService vetService) {
        super.setVetService(vetService);
    }
}
