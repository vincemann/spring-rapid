package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.jpaRepositories.OwnerRepository;
import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.service.OwnerService;

import io.github.vincemann.generic.crud.lib.service.springDataJpa.BiDirParentJPACrudService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@Profile("springdatajpa")
public class OwnerJPAService extends BiDirParentJPACrudService<Owner,Long, OwnerRepository> implements OwnerService {

    public OwnerJPAService(OwnerRepository ownerRepository) {
        super(ownerRepository);
    }

    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getJpaRepository().findByLastName(lastName);
    }

}
