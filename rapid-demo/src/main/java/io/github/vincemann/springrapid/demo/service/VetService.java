package io.github.vincemann.springrapid.demo.service;

import io.github.vincemann.springrapid.demo.model.Vet;
import io.github.vincemann.springrapid.demo.repositories.VetRepository;
import io.github.vincemann.springrapid.core.config.layers.component.ServiceComponent;
import io.github.vincemann.springrapid.core.service.CrudService;

@ServiceComponent
public interface VetService extends CrudService<Vet,Long, VetRepository> {
}
