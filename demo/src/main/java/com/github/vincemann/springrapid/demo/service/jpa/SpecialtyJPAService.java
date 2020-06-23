package com.github.vincemann.springrapid.demo.service.jpa;

import com.github.vincemann.springrapid.demo.model.Specialty;
import com.github.vincemann.springrapid.demo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.demo.service.SpecialtyService;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@ServiceComponent
public class SpecialtyJPAService extends JPACrudService<Specialty,Long, SpecialtyRepository> implements SpecialtyService {

    @Override
    protected Class<Specialty> provideEntityClass() {
        return Specialty.class;
    }
}
