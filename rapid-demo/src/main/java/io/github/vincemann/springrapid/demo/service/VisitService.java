package io.github.vincemann.springrapid.demo.service;

import io.github.vincemann.springrapid.demo.model.Visit;
import io.github.vincemann.springrapid.demo.repositories.VisitRepository;
import io.github.vincemann.springrapid.core.config.layers.component.ServiceComponent;
import io.github.vincemann.springrapid.core.service.CrudService;

@ServiceComponent
public interface VisitService extends CrudService<Visit,Long, VisitRepository> {
}
