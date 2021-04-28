package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.repositories.VetRepository;
import com.github.vincemann.springrapid.acldemo.service.VetService;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Primary
@Service
@ServiceComponent
public class JpaVetService
        extends JPACrudService<Vet,Long, VetRepository>
        implements VetService, TargetClassAware {

    @Override
    public Class<?> getTargetClass() {
        return JpaVetService.class;
    }

    @LogInteraction
    @Transactional
    @Override
    public Optional<Vet> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }
}
