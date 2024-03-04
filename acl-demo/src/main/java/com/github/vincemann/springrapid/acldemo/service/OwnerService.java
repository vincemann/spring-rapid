package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.acldemo.model.Owner;

import java.util.Optional;


public interface OwnerService extends UserService<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
    public void addPetSpectator(long permittedOwnerId, long targetOwnerId) throws EntityNotFoundException;
}
