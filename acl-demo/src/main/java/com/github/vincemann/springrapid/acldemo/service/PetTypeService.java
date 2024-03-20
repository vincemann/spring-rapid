package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.acldemo.model.PetType;

public interface PetTypeService extends CrudService<PetType,Long,PetType> {
}
