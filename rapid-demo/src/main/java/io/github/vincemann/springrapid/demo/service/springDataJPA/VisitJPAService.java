package io.github.vincemann.springrapid.demo.service.springDataJPA;

import io.github.vincemann.springrapid.demo.model.Visit;
import io.github.vincemann.springrapid.demo.repositories.VisitRepository;
import io.github.vincemann.springrapid.demo.service.VisitService;
import io.github.vincemann.springrapid.core.config.layers.component.ServiceComponent;
import io.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import org.springframework.stereotype.Service;

@Service
@ServiceComponent
public class VisitJPAService extends JPACrudService<Visit,Long, VisitRepository> implements VisitService {
}
