package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.Pet;


@Component
public interface PetService extends CrudService<Pet,Long> {

}
