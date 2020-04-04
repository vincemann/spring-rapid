package io.github.vincemann.springrapid.demo.service.springDataJPA;

import io.github.vincemann.springrapid.demo.model.Specialty;
import io.github.vincemann.springrapid.demo.repositories.SpecialtyRepository;
import io.github.vincemann.springrapid.demo.service.SpecialtyService;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import org.springframework.stereotype.Service;

@Service
@ServiceComponent
public class SpecialtyJPAService extends JPACrudService<Specialty,Long, SpecialtyRepository> implements SpecialtyService {

}
