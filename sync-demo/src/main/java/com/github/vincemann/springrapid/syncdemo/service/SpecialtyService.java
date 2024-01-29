package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.Specialty;

import java.util.Optional;

@Component
public interface SpecialtyService extends CrudService<Specialty,Long> {
    Optional<Specialty> findByDescription(String description);
}
