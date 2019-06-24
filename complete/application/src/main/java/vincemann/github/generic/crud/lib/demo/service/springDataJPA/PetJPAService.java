package vincemann.github.generic.crud.lib.demo.service.springDataJPA;

import vincemann.github.generic.crud.lib.demo.model.Pet;
import vincemann.github.generic.crud.lib.demo.service.PetService;
import vincemann.github.generic.crud.lib.demo.springDataJPA.PetRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import vincemann.github.generic.crud.lib.service.springDataJpa.BackRefSettingJPACrudService;

@Service
@Profile("springdatajpa")
public class PetJPAService extends BackRefSettingJPACrudService<Pet,Long,PetRepository> implements PetService {

    public PetJPAService(PetRepository jpaRepository) {
        super(jpaRepository, Pet.class);
    }
}
