package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.dto.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.util.Optional;


public interface OwnerService extends UserService<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
    public void addPetSpectator(long permittedOwnerId, long targetOwnerId) throws EntityNotFoundException;

    Owner update(UpdateOwnerDto dto) throws EntityNotFoundException;
}
