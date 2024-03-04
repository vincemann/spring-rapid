package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.service.AbstractSecuredUserServiceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Secured
public class SecuredVetService
        extends AbstractSecuredUserServiceDecorator<VetService, Vet,Long>
        implements VetService
{
    @Autowired
    public SecuredVetService(@Root VetService decorated) {
        super(decorated);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Vet> findByLastName(String name) {
        Optional<Vet> vet = getDecorated().findByLastName(name);
        vet.ifPresent(o -> getAclTemplate().checkPermission(o, BasePermission.READ));
        return vet;
    }
}
