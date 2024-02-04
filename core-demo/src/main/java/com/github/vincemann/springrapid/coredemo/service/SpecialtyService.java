package com.github.vincemann.springrapid.coredemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import org.springframework.stereotype.Component;

import java.util.Optional;

public interface SpecialtyService extends CrudService<Specialty,Long> {
    Optional<Specialty> findByDescription(String description);
}
