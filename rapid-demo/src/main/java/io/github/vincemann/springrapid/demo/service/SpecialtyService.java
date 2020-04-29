package io.github.vincemann.springrapid.demo.service;

import io.github.vincemann.springrapid.demo.model.Specialty;
import io.github.vincemann.springrapid.demo.repo.SpecialtyRepository;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.CrudService;

@ServiceComponent
public interface SpecialtyService extends CrudService<Specialty,Long, SpecialtyRepository> {
}
