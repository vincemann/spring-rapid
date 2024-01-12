package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.syncdemo.model.Specialty;

import java.util.Optional;

@ServiceComponent
public interface SpecialtyService extends CrudService<Specialty,Long> {
    Optional<Specialty> findByDescription(String description);
}
