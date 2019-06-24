package vincemann.github.generic.crud.lib.demo.service.springDataJPA;

import vincemann.github.generic.crud.lib.demo.model.Visit;
import vincemann.github.generic.crud.lib.demo.springDataJPA.VisitRepository;
import vincemann.github.generic.crud.lib.demo.service.VisitService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import vincemann.github.generic.crud.lib.service.springDataJpa.JPACrudService;

@Service
@Profile("springdatajpa")
public class VisitJPAService extends JPACrudService<Visit,Long, VisitRepository> implements VisitService {

    public VisitJPAService(VisitRepository jpaRepository) {
        super(jpaRepository, Visit.class);
    }
}
