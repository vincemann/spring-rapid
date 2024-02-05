package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.coredemo.service.Root;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.repo.PetRepository;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import org.springframework.aop.TargetClassAware;
import org.springframework.stereotype.Service;

@Root
@Service
@EnableAutoBiDir
public class JpaPetService extends JpaCrudService<Pet, Long, PetRepository> implements PetService, TargetClassAware {

    @Override
    public Class<?> getTargetClass() {
        return JpaPetService.class;
    }
}
