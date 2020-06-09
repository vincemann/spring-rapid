package com.github.vincemann.springrapid.demo.service;

import com.github.vincemann.springrapid.demo.model.Visit;
import com.github.vincemann.springrapid.demo.repo.VisitRepository;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.service.CrudService;

@ServiceComponent
public interface VisitService extends CrudService<Visit,Long, VisitRepository> {
}
