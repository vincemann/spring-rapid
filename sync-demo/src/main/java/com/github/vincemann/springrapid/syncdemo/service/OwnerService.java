package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.syncdemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.syncdemo.model.Owner;

import java.util.Optional;

public interface OwnerService {

    Owner create(CreateOwnerDto dto) throws EntityNotFoundException;

    Optional<Owner> find(long id);
}
