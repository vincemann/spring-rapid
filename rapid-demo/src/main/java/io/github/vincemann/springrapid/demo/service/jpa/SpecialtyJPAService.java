package io.github.vincemann.springrapid.demo.service.jpa;

import io.github.vincemann.springrapid.demo.model.Specialty;
import io.github.vincemann.springrapid.demo.repo.SpecialtyRepository;
import io.github.vincemann.springrapid.demo.service.SpecialtyService;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@ServiceComponent
public class SpecialtyJPAService extends JPACrudService<Specialty,Long, SpecialtyRepository> implements SpecialtyService {

}
