package com.github.vincemann.springrapid.authdemo.service.jpa;

import com.github.vincemann.springrapid.authdemo.model.Pet;
import com.github.vincemann.springrapid.authdemo.repo.PetRepository;
import com.github.vincemann.springrapid.authdemo.service.PetService;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.aop.TargetClassAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Qualifier("noProxy")
@Service
@ServiceComponent
public class JpaPetService extends JPACrudService<Pet, Long, PetRepository> implements PetService, TargetClassAware {
    @Override
    public Class<?> getTargetClass() {
        return JpaPetService.class;
    }
}
