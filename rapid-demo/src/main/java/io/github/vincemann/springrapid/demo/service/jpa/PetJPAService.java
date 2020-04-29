package io.github.vincemann.springrapid.demo.service.jpa;

import io.github.vincemann.springrapid.demo.model.Pet;
import io.github.vincemann.springrapid.demo.repo.PetRepository;
import io.github.vincemann.springrapid.demo.service.PetService;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Qualifier("noProxy")
@Service
@ServiceComponent
public class PetJPAService extends JPACrudService<Pet, Long, PetRepository> implements PetService {
}
