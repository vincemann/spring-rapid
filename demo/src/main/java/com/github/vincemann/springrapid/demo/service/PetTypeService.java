package com.github.vincemann.springrapid.demo.service;

import com.github.vincemann.springrapid.demo.model.PetType;
import com.github.vincemann.springrapid.demo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.service.AbstractCrudService;

@ServiceComponent
public interface PetTypeService extends AbstractCrudService<PetType,Long, PetTypeRepository> {
}
