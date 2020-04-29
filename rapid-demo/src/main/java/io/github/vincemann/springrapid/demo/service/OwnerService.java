package io.github.vincemann.springrapid.demo.service;

import io.github.vincemann.springrapid.demo.model.Owner;
import io.github.vincemann.springrapid.demo.repo.OwnerRepository;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.CrudService;

import java.util.Optional;



@ServiceComponent
public interface OwnerService extends CrudService<Owner,Long, OwnerRepository> {
    Optional<Owner> findByLastName(String lastName);
    public Optional<Owner> findOwnerOfTheYear();
}
