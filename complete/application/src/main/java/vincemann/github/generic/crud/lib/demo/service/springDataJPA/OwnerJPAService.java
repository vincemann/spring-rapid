package vincemann.github.generic.crud.lib.demo.service.springDataJPA;

import vincemann.github.generic.crud.lib.demo.model.Owner;
import vincemann.github.generic.crud.lib.demo.service.OwnerService;
import vincemann.github.generic.crud.lib.demo.springDataJPA.OwnerRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import vincemann.github.generic.crud.lib.service.springDataJpa.BackRefSettingJPACrudService;

import java.util.Optional;
@Service
@Profile("springdatajpa")
public class OwnerJPAService extends BackRefSettingJPACrudService<Owner,Long,OwnerRepository> implements OwnerService {

    public OwnerJPAService(OwnerRepository ownerRepository) {
        super(ownerRepository, Owner.class);
    }

    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getJpaRepository().findByLastName(lastName);
    }

}
