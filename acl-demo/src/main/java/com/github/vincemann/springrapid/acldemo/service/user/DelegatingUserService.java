package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
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
