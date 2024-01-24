package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.syncdemo.model.Pet;


@ServiceComponent
public interface PetService extends CrudService<Pet,Long> {

}
