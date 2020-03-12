package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.repositories.OwnerRepository;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.service.jpa.JPACrudService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Qualifier("basic")
@Service
@ServiceComponent
public class OwnerJPAService extends JPACrudService<Owner,Long,OwnerRepository> implements OwnerService {


    @Transactional
    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }

    /**
     * Owner named "42" is owner of the year
     * @return
     */
    @Override
    public Optional<Owner> findOwnerOfTheYear() {
        return getRepository().findAll().stream().filter(owner -> {
            return owner.getFirstName().equals("42");
        }).findFirst();
    }
}
