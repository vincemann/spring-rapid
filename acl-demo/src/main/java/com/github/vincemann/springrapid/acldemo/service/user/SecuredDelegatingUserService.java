package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.acldemo.service.user.AbstractDelegatingUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

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
