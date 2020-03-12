package io.github.vincemann.demo.service;

import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.demo.repositories.SpecialtyRepository;
import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.service.CrudService;

@ServiceComponent
public interface SpecialtyService extends CrudService<Specialty,Long, SpecialtyRepository> {
}
