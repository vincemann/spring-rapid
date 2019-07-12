package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.jpaRepositories.PetTypeRepository;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.service.PetTypeService;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import io.github.vincemann.generic.crud.lib.service.springDataJpa.JPACrudService;

@Service
@Profile("springdatajpa")
public class PetTypeJPAService extends JPACrudService<PetType,Long, PetTypeRepository> implements PetTypeService {

    public PetTypeJPAService(PetTypeRepository jpaRepository) {
        super(jpaRepository);
    }
}
