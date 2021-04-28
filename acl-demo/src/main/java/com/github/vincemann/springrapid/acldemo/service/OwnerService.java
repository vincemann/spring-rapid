package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Owner;

import java.util.Optional;


@ServiceComponent
public interface OwnerService extends CrudService<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
    public Optional<Owner> findOwnerOfTheYear();
}
