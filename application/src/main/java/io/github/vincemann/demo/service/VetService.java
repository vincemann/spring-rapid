package io.github.vincemann.demo.service;

import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.repositories.VetRepository;
import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.service.CrudService;

@ServiceComponent
public interface VetService extends CrudService<Vet,Long, VetRepository> {
}
