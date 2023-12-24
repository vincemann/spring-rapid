package com.github.vincemann.springrapid.coredemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;


@ServiceComponent
public interface ClinicCardService extends CrudService<ClinicCard,Long> {
}
