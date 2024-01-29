package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.acldemo.model.Specialty;

import java.util.Optional;

public interface SpecialtyService extends CrudService<Specialty,Long> {
    Optional<Specialty> findByDescription(String description);
}
