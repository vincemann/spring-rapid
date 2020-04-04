package io.github.vincemann.springrapid.demo.service;

import io.github.vincemann.springrapid.demo.model.Pet;
import io.github.vincemann.springrapid.demo.repositories.PetRepository;
import io.github.vincemann.springrapid.core.config.layers.component.ServiceComponent;
import io.github.vincemann.springrapid.core.service.CrudService;


@ServiceComponent
public interface PetService extends CrudService<Pet,Long, PetRepository> {

}
