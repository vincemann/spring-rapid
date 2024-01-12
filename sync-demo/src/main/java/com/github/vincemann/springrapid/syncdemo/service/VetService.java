package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.syncdemo.model.Vet;

import java.util.Optional;

@ServiceComponent
public interface VetService extends CrudService<Vet,Long> {

    public Optional<Vet> findByLastName(String lastName);
}
