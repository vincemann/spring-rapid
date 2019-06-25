package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.jpaRepositories.PetRepository;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.PetService;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import io.github.vincemann.generic.crud.lib.service.springDataJpa.BackRefSettingJPACrudService;

@Service
@Profile("springdatajpa")
public class PetJPAService extends BackRefSettingJPACrudService<Pet,Long, PetRepository> implements PetService {

    public PetJPAService(PetRepository jpaRepository) {
        super(jpaRepository, Pet.class);
    }
}
