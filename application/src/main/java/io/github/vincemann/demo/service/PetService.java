package io.github.vincemann.demo.service;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.repositories.PetRepository;
import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.service.CrudService;


@ServiceComponent
public interface PetService extends CrudService<Pet,Long, PetRepository> {

}
