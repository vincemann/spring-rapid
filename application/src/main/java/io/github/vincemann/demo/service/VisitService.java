package io.github.vincemann.demo.service;

import io.github.vincemann.demo.model.Visit;
import io.github.vincemann.demo.repositories.VisitRepository;
import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.service.CrudService;

@ServiceComponent
public interface VisitService extends CrudService<Visit,Long, VisitRepository> {
}
