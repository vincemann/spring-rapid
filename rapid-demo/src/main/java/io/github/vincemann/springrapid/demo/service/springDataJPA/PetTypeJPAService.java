package io.github.vincemann.springrapid.demo.service.springDataJPA;

import io.github.vincemann.springrapid.demo.repositories.PetTypeRepository;
import io.github.vincemann.springrapid.demo.model.PetType;
import io.github.vincemann.springrapid.demo.service.PetTypeService;

import io.github.vincemann.springrapid.core.config.layers.component.ServiceComponent;
import org.springframework.stereotype.Service;
import io.github.vincemann.springrapid.core.service.jpa.JPACrudService;

@Service
@ServiceComponent
public class PetTypeJPAService extends JPACrudService<PetType,Long, PetTypeRepository> implements PetTypeService {

}
