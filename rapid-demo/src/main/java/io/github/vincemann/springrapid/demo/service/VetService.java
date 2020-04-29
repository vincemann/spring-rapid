package io.github.vincemann.springrapid.demo.service;

import io.github.vincemann.springrapid.demo.model.Vet;
import io.github.vincemann.springrapid.demo.repo.VetRepository;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.CrudService;

@ServiceComponent
public interface VetService extends CrudService<Vet,Long, VetRepository> {
}
