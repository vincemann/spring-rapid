package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.syncdemo.model.Toy;

import java.util.Optional;

@ServiceComponent
public interface ToyService extends CrudService<Toy,Long> {
    Optional<Toy> findByName(String name);
}