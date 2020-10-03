package com.github.vincemann.springrapid.demo.service;

import com.github.vincemann.springrapid.core.service.AbstractCrudService;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.demo.model.Owner;
import com.github.vincemann.springrapid.demo.repo.OwnerRepository;

import java.util.Optional;



@ServiceComponent
public interface OwnerService extends AbstractCrudService<Owner,Long, OwnerRepository> {
    Optional<Owner> findByLastName(String lastName);
    public Optional<Owner> findOwnerOfTheYear();
}
