package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acldemo.dto.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.auth.service.SecuredUserServiceDecorator;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.ex.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Secured
public class SecuredOwnerService
        extends SecuredUserServiceDecorator<OwnerService, Owner,Long>
        implements OwnerService {


    @Autowired
    public SecuredOwnerService(@Root OwnerService decorated) {
        super(decorated);
    }

    @Transactional
    @Override
    public Owner update(UpdateOwnerDto dto) throws EntityNotFoundException {
        getAclTemplate().checkPermission(dto.getId(),Owner.class, BasePermission.WRITE);
        return getDecorated().update(dto);
    }

    @Transactional(readOnly = true)
    @Override
    @PostAuthorize("returnObject.isPresent() ? hasPermission(returnObject.get(), 'read') : true")
    public Optional<Owner> findByLastName(String name) {
        return getDecorated().findByLastName(name);
    }

    @Transactional
    @Override
    @PreAuthorize("hasPermission(#targetOwnerId, 'com.github.vincemann.springrapid.acldemo.model.Owner', 'administration')")
    public void addPetSpectator(long permittedOwnerId, long targetOwnerId) throws EntityNotFoundException {
        getDecorated().addPetSpectator(permittedOwnerId,targetOwnerId);
    }
}
