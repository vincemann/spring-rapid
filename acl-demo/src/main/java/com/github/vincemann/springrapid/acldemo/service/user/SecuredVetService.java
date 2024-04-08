package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.service.SecuredUserServiceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Secured
public class SecuredVetService
        extends SecuredUserServiceDecorator<VetService, Vet,Long>
        implements VetService
{
    @Autowired
    public SecuredVetService(@Root VetService decorated) {
        super(decorated);
    }

    @Transactional(readOnly = true)
    @Override
    @PostAuthorize("returnObject.isPresent() ? hasPermission(returnObject.get(), 'read') : true")
    public Optional<Vet> findByLastName(String name) {
        return getDecorated().findByLastName(name);
    }
}
