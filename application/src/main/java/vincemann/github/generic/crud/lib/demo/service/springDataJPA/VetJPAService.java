package vincemann.github.generic.crud.lib.demo.service.springDataJPA;

import vincemann.github.generic.crud.lib.demo.model.Vet;
import vincemann.github.generic.crud.lib.demo.service.VetService;
import vincemann.github.generic.crud.lib.demo.springDataJPA.VetRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import vincemann.github.generic.crud.lib.service.springDataJpa.JPACrudService;

@Service
@Profile("springdatajpa")
public class VetJPAService extends JPACrudService<Vet,Long, VetRepository> implements VetService {

    public VetJPAService(VetRepository vetRepository) {
        super(vetRepository, Vet.class);
    }
}
