package com.github.vincemann.springrapid.acldemo.owner;

import com.github.vincemann.springrapid.acldemo.owner.dto.UpdateOwnerDto;
import com.github.vincemann.springrapid.acldemo.owner.Owner;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;

import java.util.Optional;


public interface OwnerService extends UserService<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
    public void addPetSpectator(long permittedOwnerId, long targetOwnerId) throws EntityNotFoundException;

    Owner update(UpdateOwnerDto dto) throws EntityNotFoundException;
}
