package com.github.vincemann.springrapid.demo.service;

import com.github.vincemann.springrapid.demo.model.Vet;
import com.github.vincemann.springrapid.demo.repo.VetRepository;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.service.CrudService;

@ServiceComponent
public interface VetService extends CrudService<Vet,Long, VetRepository> {
}
