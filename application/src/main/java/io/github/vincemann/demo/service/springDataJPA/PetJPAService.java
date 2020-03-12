package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.repositories.PetRepository;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.service.jpa.JPACrudService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Qualifier("basic")
@Service
@ServiceComponent
public class PetJPAService extends JPACrudService<Pet, Long, PetRepository> implements PetService {
}
