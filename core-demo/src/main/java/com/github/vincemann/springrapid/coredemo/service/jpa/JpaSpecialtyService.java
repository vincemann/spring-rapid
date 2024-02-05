package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.autobidir.EnableAutoBiDir;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.repo.SpecialtyRepository;
import com.github.vincemann.springrapid.coredemo.service.SpecialtyService;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
@EnableAutoBiDir
public class JpaSpecialtyService
        extends JpaCrudService<Specialty,Long, SpecialtyRepository>
        implements SpecialtyService
{

    @Override
    public Optional<Specialty> findByDescription(String description) {
        return getRepository().findByDescription(description);
    }

    @Override
    public Class<?> getTargetClass() {
        return JpaSpecialtyService.class;
    }
}
