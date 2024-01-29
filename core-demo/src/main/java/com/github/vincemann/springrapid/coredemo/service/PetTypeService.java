package com.github.vincemann.springrapid.coredemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coredemo.model.PetType;
import org.springframework.stereotype.Component;

@Component
public interface PetTypeService extends CrudService<PetType,Long> {
}
