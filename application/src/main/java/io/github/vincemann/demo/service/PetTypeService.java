package io.github.vincemann.demo.service;

import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.repositories.PetTypeRepository;
import io.github.vincemann.generic.crud.lib.service.CrudService;

public interface PetTypeService extends CrudService<PetType,Long, PetTypeRepository> {
}
