package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Specialty;
import com.github.vincemann.springrapid.acldemo.repositories.SpecialtyRepository;
import com.github.vincemann.springrapid.acldemo.service.SpecialtyService;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
@ServiceComponent
public class JpaSpecialtyService extends JPACrudService<Specialty,Long, SpecialtyRepository> implements SpecialtyService, TargetClassAware {

    @Override
    public Optional<Specialty> findByDescription(String description) {
        return getRepository().findByDescription(description);
    }

    @Override
    public Class<?> getTargetClass() {
        return JpaSpecialtyService.class;
    }
}
