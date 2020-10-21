package com.github.vincemann.springrapid.authdemo.service.jpa;

import com.github.vincemann.springrapid.authdemo.model.Specialty;
import com.github.vincemann.springrapid.authdemo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.authdemo.service.SpecialtyService;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@ServiceComponent
public class JpaSpecialtyService extends JPACrudService<Specialty,Long, SpecialtyRepository> implements SpecialtyService, TargetClassAware {

    @Override
    public Class<?> getTargetClass() {
        return JpaSpecialtyService.class;
    }
}
