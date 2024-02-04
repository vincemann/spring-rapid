package com.github.vincemann.springrapid.coredemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Toy;

import java.util.Optional;

public interface ToyService extends CrudService<Toy,Long> {
    Optional<Toy> findByName(String name);
}
