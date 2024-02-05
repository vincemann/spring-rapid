package com.github.vincemann.springrapid.syncdemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.Specialty;
import com.github.vincemann.springrapid.syncdemo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.syncdemo.service.SpecialtyService;
import org.springframework.aop.TargetClassAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
@EnableAutoBiDir
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
