package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;


@ServiceComponent
public interface ClinicCardService extends CrudService<ClinicCard,Long> {
}
