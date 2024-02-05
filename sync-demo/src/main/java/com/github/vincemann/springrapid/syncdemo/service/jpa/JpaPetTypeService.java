package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.PetType;
import com.github.vincemann.springrapid.syncdemo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.syncdemo.service.PetTypeService;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@EnableAutoBiDir
public class JpaPetTypeService
        extends JPACrudService<PetType,Long, PetTypeRepository>
                implements PetTypeService {

    @Override
    public Class<?> getTargetClass() {
        return JpaPetTypeService.class;
    }
}
