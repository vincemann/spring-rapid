package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.jpaRepositories.PetRepository;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.PetService;

import io.github.vincemann.generic.crud.lib.service.springDataJpa.BiDirChildJPACrudService;
import io.github.vincemann.generic.crud.lib.service.springDataJpa.JPACrudService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("springdatajpa")
public class PetJPAService extends BiDirChildJPACrudService<Pet,Long, PetRepository> implements PetService {

    public PetJPAService(PetRepository jpaRepository) {
        super(jpaRepository);
    }
}
