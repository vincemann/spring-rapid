package com.github.vincemann.springrapid.demo.service;

import com.github.vincemann.springrapid.demo.model.Specialty;
import com.github.vincemann.springrapid.demo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.service.CrudService;

@ServiceComponent
public interface SpecialtyService extends CrudService<Specialty,Long, SpecialtyRepository> {
}
