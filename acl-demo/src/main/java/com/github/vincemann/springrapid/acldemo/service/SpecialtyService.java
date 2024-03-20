package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.dto.CreateSpecialtyDto;
import com.github.vincemann.springrapid.acldemo.dto.ReadSpecialtyDto;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.acldemo.model.Specialty;

import java.util.Optional;

public interface SpecialtyService extends CrudService<Specialty,Long, CreateSpecialtyDto> {
    Optional<Specialty> findByDescription(String description);
}
