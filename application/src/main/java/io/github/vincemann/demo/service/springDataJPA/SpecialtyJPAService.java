package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.repositories.SpecialtyRepository;
import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.demo.service.SpecialtyService;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import io.github.vincemann.generic.crud.lib.service.jpa.JPACrudService;

@Service
@Profile("springdatajpa")
public class SpecialtyJPAService extends JPACrudService<Specialty,Long, SpecialtyRepository> implements SpecialtyService {

    public SpecialtyJPAService(SpecialtyRepository jpaRepository) {
        super(jpaRepository);
    }
}
