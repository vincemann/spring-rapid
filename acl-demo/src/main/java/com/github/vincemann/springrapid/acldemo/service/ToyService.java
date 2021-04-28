package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.acldemo.model.Toy;

import java.util.Optional;

public interface ToyService extends CrudService<Toy,Long> {
    Optional<Toy> findByName(String name);
}
