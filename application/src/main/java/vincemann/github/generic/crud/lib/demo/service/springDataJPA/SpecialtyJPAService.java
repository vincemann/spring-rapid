package vincemann.github.generic.crud.lib.demo.service.springDataJPA;

import vincemann.github.generic.crud.lib.demo.model.Specialty;
import vincemann.github.generic.crud.lib.demo.service.SpecialtyService;
import vincemann.github.generic.crud.lib.demo.springDataJPA.SpecialtyRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import vincemann.github.generic.crud.lib.service.springDataJpa.JPACrudService;

@Service
@Profile("springdatajpa")
public class SpecialtyJPAService extends JPACrudService<Specialty,Long, SpecialtyRepository> implements SpecialtyService {

    public SpecialtyJPAService(SpecialtyRepository jpaRepository) {
        super(jpaRepository, Specialty.class);
    }
}
