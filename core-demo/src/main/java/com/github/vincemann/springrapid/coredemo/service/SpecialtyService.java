package com.github.vincemann.springrapid.coredemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;

import java.util.Optional;

@ServiceComponent
public interface SpecialtyService extends CrudService<Specialty,Long> {
    Optional<Specialty> findByDescription(String description);
}
