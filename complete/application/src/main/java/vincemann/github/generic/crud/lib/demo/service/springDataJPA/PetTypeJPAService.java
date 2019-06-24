package vincemann.github.generic.crud.lib.demo.service.springDataJPA;

import vincemann.github.generic.crud.lib.demo.model.PetType;
import vincemann.github.generic.crud.lib.demo.service.PetTypeService;
import vincemann.github.generic.crud.lib.demo.springDataJPA.PetTypeRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import vincemann.github.generic.crud.lib.service.springDataJpa.JPACrudService;

@Service
@Profile("springdatajpa")
public class PetTypeJPAService extends JPACrudService<PetType,Long,PetTypeRepository> implements PetTypeService {

    public PetTypeJPAService(PetTypeRepository jpaRepository) {
        super(jpaRepository, PetType.class);
    }
}
