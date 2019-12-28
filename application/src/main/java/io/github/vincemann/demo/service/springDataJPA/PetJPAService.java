package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.repositories.PetRepository;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.demo.service.plugin.AclPlugin;
import io.github.vincemann.generic.crud.lib.service.jpa.JPACrudService;
import io.github.vincemann.generic.crud.lib.service.plugin.BiDirChildPlugin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("springdatajpa")
@Qualifier("basic")
public class PetJPAService extends JPACrudService<Pet, Long, PetRepository> implements PetService {
}
