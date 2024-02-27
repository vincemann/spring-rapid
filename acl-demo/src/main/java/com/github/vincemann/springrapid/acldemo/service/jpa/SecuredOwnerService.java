package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acl.service.SecuredCrudServiceDecorator;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Secured
public class SecuredOwnerService
        extends SecuredCrudServiceDecorator<OwnerService, Owner,Long>
        implements OwnerService{

    @Autowired
    public SecuredOwnerService(OwnerService decorated) {
        super(decorated);
    }

    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return Optional.empty();
    }

    @Override
    public Optional<Owner> findOwnerOfTheYear() {
        return Optional.empty();
    }
}
