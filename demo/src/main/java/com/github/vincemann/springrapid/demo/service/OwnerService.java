package com.github.vincemann.springrapid.demo.service;

import com.github.vincemann.springrapid.core.advice.log.LogInteraction;
import com.github.vincemann.springrapid.demo.model.Owner;
import com.github.vincemann.springrapid.demo.repo.OwnerRepository;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.service.CrudService;

import java.util.Optional;



@ServiceComponent
@LogInteraction
public interface OwnerService extends CrudService<Owner,Long, OwnerRepository> {
    Optional<Owner> findByLastName(String lastName);
    public Optional<Owner> findOwnerOfTheYear();
}
