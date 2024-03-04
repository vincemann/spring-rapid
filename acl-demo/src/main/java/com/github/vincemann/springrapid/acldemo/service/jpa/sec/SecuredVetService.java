package com.github.vincemann.springrapid.acldemo.service.jpa.sec;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.auth.service.AbstractSecuredUserServiceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public class SecuredVetService
        extends AbstractSecuredUserServiceDecorator<VetService, Vet, Long>
        implements VetService
{

    public SecuredVetService(VetService decorated) {
        super(decorated);
    }

    @Transactional
    @Override
    public Optional<Vet> findByLastName(String lastName) {
        Optional<Vet> vet = getDecorated().findByLastName(lastName);
        vet.ifPresent(v -> getAclTemplate().checkPermission(v, BasePermission.READ));
        return vet;
    }
}
