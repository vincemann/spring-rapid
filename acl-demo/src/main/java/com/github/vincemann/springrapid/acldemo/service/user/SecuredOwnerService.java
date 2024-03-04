package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.service.AbstractSecuredUserServiceDecorator;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Secured
@com.github.vincemann.springrapid.acldemo.Owner
public class SecuredOwnerService
        extends AbstractSecuredUserServiceDecorator<OwnerService, Owner,Long>
        implements OwnerService {

    private final OwnerRepository ownerRepository;

    @Autowired
    public SecuredOwnerService(@Root OwnerService decorated,
                               OwnerRepository ownerRepository) {
        super(decorated);
        this.ownerRepository = ownerRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Owner> findByLastName(String name) {
        Optional<Owner> owner = getDecorated().findByLastName(name);
        owner.ifPresent(o -> getAclTemplate().checkPermission(o, BasePermission.READ));
        return owner;
    }

    @Transactional
    @Override
    public void addPetSpectator(long permittedOwnerId, long targetOwnerId) throws EntityNotFoundException {
        // must have admin permission about target owner for that operation
        getAclTemplate().checkPermission(targetOwnerId, Owner.class,BasePermission.ADMINISTRATION);
        getDecorated().addPetSpectator(permittedOwnerId,targetOwnerId);
    }
}
