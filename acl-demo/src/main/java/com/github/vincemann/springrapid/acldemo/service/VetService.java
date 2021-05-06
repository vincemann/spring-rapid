package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Vet;

import java.util.Optional;

@ServiceComponent
public interface VetService extends CrudService<Vet,Long> {

    public Optional<Vet> findByLastName(String lastName);
    public void giveOwnerReadPermissionForVisit(Owner owner, Visit visit);
}
