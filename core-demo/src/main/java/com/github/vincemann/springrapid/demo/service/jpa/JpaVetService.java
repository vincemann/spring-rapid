package com.github.vincemann.springrapid.demo.service.jpa;

import com.github.vincemann.springrapid.demo.model.Vet;
import com.github.vincemann.springrapid.demo.repo.VetRepository;
import com.github.vincemann.springrapid.demo.service.VetService;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@ServiceComponent
public class JpaVetService extends JPACrudService<Vet,Long, VetRepository> implements VetService, TargetClassAware {

    @Override
    public Class<?> getTargetClass() {
        return JpaVetService.class;
    }
}
