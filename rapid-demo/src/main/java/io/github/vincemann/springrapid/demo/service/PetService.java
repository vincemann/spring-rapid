package io.github.vincemann.springrapid.demo.service;

import io.github.vincemann.springrapid.demo.model.Pet;
import io.github.vincemann.springrapid.demo.repo.PetRepository;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.CrudService;


@ServiceComponent
public interface PetService extends CrudService<Pet,Long, PetRepository> {

}
