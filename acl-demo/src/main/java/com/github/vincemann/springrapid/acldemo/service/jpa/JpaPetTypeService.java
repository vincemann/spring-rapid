package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.PetType;
import com.github.vincemann.springrapid.acldemo.repositories.PetTypeRepository;
import com.github.vincemann.springrapid.acldemo.service.PetTypeService;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@ServiceComponent
public class JpaPetTypeService extends JPACrudService<PetType,Long, PetTypeRepository> implements PetTypeService, TargetClassAware {

    @Override
    public Class<?> getTargetClass() {
        return JpaPetTypeService.class;
    }
}
