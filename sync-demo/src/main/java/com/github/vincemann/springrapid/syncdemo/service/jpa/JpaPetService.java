package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.repo.PetRepository;
import com.github.vincemann.springrapid.syncdemo.service.PetService;
import com.github.vincemann.springrapid.syncdemo.service.Root;
import org.springframework.aop.TargetClassAware;
import org.springframework.stereotype.Service;

import java.util.Set;

@Root
@Service
@EnableAutoBiDir
public class JpaPetService
        extends JPACrudService<Pet, Long, PetRepository>
                implements PetService {

    @Override
    public Class<?> getTargetClass() {
        return JpaPetService.class;
    }

}
