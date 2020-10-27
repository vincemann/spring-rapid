package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.coredemo.service.SpecialtyService;
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
