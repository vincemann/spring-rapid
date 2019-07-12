package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.jpaRepositories.VetRepository;
import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.service.VetService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import io.github.vincemann.generic.crud.lib.service.springDataJpa.JPACrudService;

@Service
@Profile("springdatajpa")
public class VetJPAService extends JPACrudService<Vet,Long, VetRepository> implements VetService {

    public VetJPAService(VetRepository vetRepository) {
        super(vetRepository);
    }
}
