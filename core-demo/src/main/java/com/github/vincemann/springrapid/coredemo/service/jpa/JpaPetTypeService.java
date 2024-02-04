package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.coredemo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.coredemo.model.PetType;
import com.github.vincemann.springrapid.coredemo.service.PetTypeService;

import org.springframework.stereotype.Component;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.github.vincemann.springrapid.core.service.JPACrudService;

@Primary
@Service
@EnableAutoBiDir
public class JpaPetTypeService extends JPACrudService<PetType,Long, PetTypeRepository> implements PetTypeService, TargetClassAware {

    @Override
    public Class<?> getTargetClass() {
        return JpaPetTypeService.class;
    }
}
