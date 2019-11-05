package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.repositories.PetRepository;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.PetService;

import io.github.vincemann.demo.service.plugins.AclPlugin;
import io.github.vincemann.generic.crud.lib.service.decorator.DecorationQualifier;
import io.github.vincemann.generic.crud.lib.service.plugin.BiDirChildPlugin;
import io.github.vincemann.generic.crud.lib.service.jpa.JPACrudService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("springdatajpa")
@Qualifier(DecorationQualifier.UNDECORATED)
public class PetJPAService extends JPACrudService<Pet, Long, PetRepository> implements PetService {

    public PetJPAService(PetRepository jpaRepository
    ) {
        super(
                jpaRepository
        );
    }
}
