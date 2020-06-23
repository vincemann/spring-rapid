package com.github.vincemann.springrapid.demo.service.jpa;

import com.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.demo.model.PetType;
import com.github.vincemann.springrapid.demo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.demo.service.PetTypeService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@ServiceComponent
public class PetTypeJPAService extends JPACrudService<PetType,Long, PetTypeRepository> implements PetTypeService {

    @Override
    protected Class<PetType> provideEntityClass() {
        return PetType.class;
    }
}
