package io.github.vincemann.springrapid.demo.service.springDataJPA;

import io.github.vincemann.springrapid.demo.model.Vet;
import io.github.vincemann.springrapid.demo.repositories.VetRepository;
import io.github.vincemann.springrapid.demo.service.VetService;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import org.springframework.stereotype.Service;

@Service
@ServiceComponent
public class VetJPAService extends JPACrudService<Vet,Long, VetRepository> implements VetService {

}
