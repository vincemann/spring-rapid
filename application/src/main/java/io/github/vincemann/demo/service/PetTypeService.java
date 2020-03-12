package io.github.vincemann.demo.service;

import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.repositories.PetTypeRepository;
import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.service.CrudService;

@ServiceComponent
public interface PetTypeService extends CrudService<PetType,Long, PetTypeRepository> {
}
