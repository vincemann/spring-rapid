package io.github.vincemann.demo.service.springDataJPA;

import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.repositories.VetRepository;
import io.github.vincemann.demo.service.VetService;
import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.service.jpa.JPACrudService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@ServiceComponent
public class VetJPAService extends JPACrudService<Vet,Long, VetRepository> implements VetService {

}
