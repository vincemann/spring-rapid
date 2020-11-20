package com.github.vincemann.springrapid.coredemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coredemo.model.Visit;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;

@ServiceComponent
public interface VisitService extends CrudService<Visit,Long> {
}
