package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.repositories.VetRepository;
import io.github.vincemann.demo.service.VetService;
import io.github.vincemann.demo.service.plugins.AclPlugin;
import io.github.vincemann.demo.service.plugins.PersonNameSavingPlugin;
import io.github.vincemann.generic.crud.lib.service.jpa.JPACrudService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("springdatajpa")
public class VetJPAService extends JPACrudService<Vet,Long, VetRepository> implements VetService {

    public VetJPAService(
                         VetRepository vetRepository,
                         AclPlugin aclPlugin,
                         PersonNameSavingPlugin personNameSavingPlugin
    ) {
        super(
                vetRepository,
                aclPlugin,
                personNameSavingPlugin
        );
    }
}
