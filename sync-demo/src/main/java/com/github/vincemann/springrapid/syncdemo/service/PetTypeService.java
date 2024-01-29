package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.PetType;

@Component
public interface PetTypeService extends CrudService<PetType,Long> {
}
