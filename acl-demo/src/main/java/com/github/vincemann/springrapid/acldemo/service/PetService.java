package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.ParentAwareService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Pet;


public interface PetService extends CrudService<Pet,Long> {

}
