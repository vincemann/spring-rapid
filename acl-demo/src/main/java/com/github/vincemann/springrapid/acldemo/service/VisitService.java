package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Visit;

@ServiceComponent
public interface VisitService extends CrudService<Visit,Long> {
}
