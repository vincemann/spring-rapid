package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.syncdemo.model.Toy;

import java.util.Optional;

@Component
public interface ToyService extends CrudService<Toy,Long> {
    Optional<Toy> findByName(String name);
}
